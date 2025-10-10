package com.wallet;

import com.wallet.bootstrap.Startup;
import com.wallet.config.Config;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        Startup.checkDatabaseConnection();

        HttpServer server = HttpServer.create(new InetSocketAddress(Config.httpPort()), 0);
        Routes.register(server);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            try {
                server.stop(1);
            } catch (Throwable ignore) {
            }
        }));

        server.start();
        System.out.println("HTTP server listening on :" + Config.httpPort());
    }
}
