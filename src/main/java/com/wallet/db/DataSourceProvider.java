package com.wallet.db;

import com.wallet.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {
    private static final HikariDataSource DS;

    static {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(Config.dbUrl());
        hc.setUsername(Config.dbUser());
        hc.setPassword(Config.dbPass());
        hc.setMaximumPoolSize(Config.dbPoolMax());
        hc.setIdleTimeout(Config.dbPoolIdleTimeoutMs());
        hc.setMaxLifetime(Config.dbPoolMaxLifetimeMs());
        DS = new HikariDataSource(hc);
    }

    public static DataSource get() {
        return DS;
    }

    public static void close() {
        DS.close();
    }
}
