package com.example.reddit.mappers;

public class AuthenticationResponse {
    public String username;
    public String token;

    public AuthenticationResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
