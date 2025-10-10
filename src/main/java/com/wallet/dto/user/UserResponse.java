package com.wallet.dto.user;

public class UserResponse {
    private long id;
    private String username;
    private String email;

    public UserResponse(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }
}
