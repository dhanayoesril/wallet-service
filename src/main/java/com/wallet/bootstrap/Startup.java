package com.wallet.bootstrap;

import com.wallet.config.Config;
import com.wallet.db.Database;

public class Startup {
    public static void checkDatabaseConnection() throws Exception {
        System.out.println("Connecting to: " + Config.dbUrl() + " as " + Config.dbUser());
        int ok = Database.ping();
        System.out.println("DB connected, SELECT 1 = " + ok);
    }
}
