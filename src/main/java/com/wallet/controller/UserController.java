package com.wallet.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wallet.dto.user.CreateUserRequest;
import com.wallet.dto.user.UserResponse;
import com.wallet.http.HttpUtil;
import com.wallet.http.Response;
import com.wallet.service.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public final class UserController {
    private static final UserService svc = new UserService();
    private static final Gson gson = new Gson();

    public static void list(HttpExchange ex) {
        try {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.getResponseHeaders().set("Allow", "GET");
                HttpUtil.writeJson(ex, 405, Response.fail("method_not_allowed"));
                return;
            }

            List<UserResponse> users = svc.listUsersDto();
            HttpUtil.writeJson(ex, 200, Response.ok(users));

        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.writeJson(ex, 500, Response.fail("internal_error"));
        }
    }

    public static void create(HttpExchange ex) {
        try {
            if ("!POST".equalsIgnoreCase(ex.getRequestMethod())) {
                HttpUtil.writeJson(ex, 405, Response.fail("method_not_allowed"));
                return;
            }

            CreateUserRequest body;
            try (var reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
                body = gson.fromJson(reader, CreateUserRequest.class);
            } catch (JsonSyntaxException e) {
                HttpUtil.writeJson(ex, 400, Response.fail("invalid_json"));
                return;
            }

            UserResponse created = svc.createUser(body);
            HttpUtil.writeJson(ex, 201, Response.ok(created));
        } catch (IllegalArgumentException iae) {
            HttpUtil.writeJson(ex, 400, Response.fail(iae.getMessage()));
        } catch (SQLException sqle) {
            // 23505 = unique_violation (Postgres) → username/email duplikat
            if ("23505".equals(sqle.getSQLState())) {
                HttpUtil.writeJson(ex, 409, Response.fail("duplicate_username_or_email"));
            } else {
                HttpUtil.writeJson(ex, 500, Response.fail("db_error"));
            }
        } catch (Exception e) {
            HttpUtil.writeJson(ex, 500, Response.fail("internal_error"));
        }
    }
}
