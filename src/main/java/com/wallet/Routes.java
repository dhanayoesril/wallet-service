package com.wallet;

import com.sun.net.httpserver.HttpServer;
import com.wallet.controller.HealthController;
import com.wallet.controller.UserController;
import com.wallet.controller.TransactionController;

public class Routes {
    public static void register(HttpServer server) {
        server.createContext("/health", HealthController::health);

        server.createContext("/users", ex -> {
            System.out.println(">> " + ex.getRequestMethod() + " " + ex.getRequestURI().getPath());
            try {
                switch (ex.getRequestMethod()) {
                    case "GET":
                        UserController.list(ex);
                        break;
                    case "POST":
                        UserController.create(ex);
                        break;
                }
            } finally {
                ex.close();
            }
        });

        server.createContext("/transactions/reserve", ex -> {
            try {
                switch (ex.getRequestMethod()) {
                    case "POST":
                        TransactionController.reserve(ex);
                        break;
                    case "OPTIONS":
                        ex.getResponseHeaders().add("Allow", "POST, OPTIONS");
                        ex.sendResponseHeaders(204, -1);
                        break;
                    default:
                        ex.getResponseHeaders().add("Allow", "POST, OPTIONS");
                        ex.sendResponseHeaders(405, -1);
                }
            } finally {
                ex.close();
            }
        });
    }
}
