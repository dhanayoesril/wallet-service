package com.wallet.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.wallet.dto.transaction.ReserveRequest;
import com.wallet.dto.transaction.ReserveResponse;
import com.wallet.http.HttpUtil;
import com.wallet.http.Response;
import com.wallet.service.TransactionService;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class TransactionController {
    private static final TransactionService svc = new TransactionService();
    private static final Gson gson = new Gson();

    public static void reserve(HttpExchange ex) {
        try (var reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
            ReserveRequest body = gson.fromJson(reader, ReserveRequest.class);
            ReserveResponse res = svc.reserve(body);
            HttpUtil.writeJson(ex, 201, Response.ok(res));
        } catch (JsonSyntaxException e) {
            HttpUtil.writeJson(ex, 400, Response.fail("invalid_json"));
        } catch (IllegalArgumentException iae) {
            HttpUtil.writeJson(ex, 400, Response.fail(iae.getMessage()));
        } catch (SQLException sqle) {
            if ("23514".equals(sqle.getSQLState()) && "insufficient_funds".equals(sqle.getMessage())) {
                HttpUtil.writeJson(ex, 409, Response.fail("insufficient_funds"));
            } else {
                HttpUtil.writeJson(ex, 500, Response.fail("db_error"));
            }
        } catch (Exception e) {
            HttpUtil.writeJson(ex, 500, Response.fail("internal_error"));
        }
    }
}
