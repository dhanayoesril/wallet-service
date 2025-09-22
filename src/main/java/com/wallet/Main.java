package com.wallet;

import com.wallet.config.Config;
import com.wallet.db.Database;
import com.wallet.db.Database.UserRow;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static final Gson GSON = new Gson();

    public static void main(String[] args) throws Exception {
        System.out.println("Connecting to: " + Config.dbUrl() + " as " + Config.dbUser());
        int ok = Database.ping();
        System.out.println("DB connected, SELECT 1 = " + ok);

        HttpServer server = HttpServer.create(new InetSocketAddress(Config.httpPort()), 0);
        server.createContext("/health", Main::health);
        server.createContext("/users", Main::getUsers);
        server.start();
        System.out.println("HTTP server listening on :" + Config.httpPort());
    }

    private static void health(HttpExchange ex) {
        try {
            String body = "{\"status\":\"ok\"}";
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getUsers(HttpExchange ex) {
        try {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                writeJson(ex, 405, new Err("method_not_allowed"));
                return;
            }
            List<UserRow> rows = Database.listUsers();
            writeJson(ex, 200, rows);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                writeJson(ex, 500, new Err("internal_error"));
            } catch (IOException ignored) {
            }
        }
    }

    private static void writeJson(HttpExchange ex, int status, Object body) throws IOException {
        byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (var os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    static class Msg {
        public String status;

        Msg(String s) {
            this.status = s;
        }
    }

    static class Err {
        public String error;

        Err(String e) {
            this.error = e;
        }
    }

}
