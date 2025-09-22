package com.wallet.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv env = Dotenv.configure()
        .ignoreIfMissing()
        .load();

    public static String getEnv(String key, String def) {
        return env.get(key, def);
    }

    public static String dbUrl() {
        String host = getEnv("DB_HOST", "localhost");
        String port = getEnv("DB_PORT", "5432");
        String name = getEnv("DB_name", "wallet_db");

        return "jdbc:postgresql://" + host + ":" + port + "/" + name;
    }

    public static String dbUser() {
        return getEnv("DB_USER", "user");
    }

    public static String dbPass() {
        return getEnv("DB_PASS", "secret");
    }

    public static int httpPort() {
        return Integer.parseInt(getEnv("PORT", "8080"));
    }
}
