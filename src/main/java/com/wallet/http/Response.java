package com.wallet.http;

public class Response<T> {
    public boolean success;
    public T data;
    public String error;

    private Response(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(true, data, null);
    }

    public static <T> Response<T> fail(String error) {
        return new Response<>(false, null, error);
    }
}
