package com.wallet.controller;

import com.wallet.http.HttpUtil;
import com.wallet.http.Response;
import com.sun.net.httpserver.HttpExchange;

public class HealthController {
    public static void health(HttpExchange ex) {
        HttpUtil.writeJson(ex, 200, Response.ok("ok"));
    }
}
