package com.example.reddit.controllers;

import com.example.reddit.mappers.AuthenticationCheckResponse;
import com.example.reddit.mappers.AuthenticationResponse;
import com.example.reddit.mappers.LoginRequest;
import com.example.reddit.mappers.RegisterRequest;
import com.example.reddit.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //@CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        authService.signup(registerRequest);
        return new ResponseEntity<>("User was created", HttpStatus.CREATED);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable("token") String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account is activated", HttpStatus.OK);
    }

    //@CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(HttpServletResponse response,
                                                        @RequestBody LoginRequest loginRequest){
        AuthenticationResponse authenticationResponse = authService.login(loginRequest, response);
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

    @GetMapping("/authenticated")
    public ResponseEntity<AuthenticationCheckResponse> checkAuthentication(){
        return new ResponseEntity<>(authService.checkAuthentication(), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public void logout(){
        authService.logout();
    }
}
