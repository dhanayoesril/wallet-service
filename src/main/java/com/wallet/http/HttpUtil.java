package com.wallet.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    private static final Gson G = new Gson();

    public static void writeJson(HttpExchange ex, int status, Object body) {
        try {
            byte[] bytes = G.toJson(body).getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(status, bytes.length);
            OutputStream os = ex.getResponseBody();
            try {
                os.write(bytes);
            } finally {
                os.close();
            }
        } catch (Exception ignored) {
        }
    }
}
