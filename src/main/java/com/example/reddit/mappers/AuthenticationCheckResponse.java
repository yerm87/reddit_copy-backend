package com.example.reddit.mappers;

import com.example.reddit.entities.User;

import java.util.List;

public class AuthenticationCheckResponse {
    private boolean authenticated;
    private String username;


    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
