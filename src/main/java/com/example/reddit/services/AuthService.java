package com.example.reddit.services;

import com.example.reddit.entities.*;
import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.mappers.*;
import com.example.reddit.repos.UserRepository;
import com.example.reddit.repos.VerificationTokenRepository;
import com.example.reddit.repos.VoteRepository;
import com.example.reddit.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final VoteRepository voteRepository;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       MailService mailService, AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider, VoteRepository voteRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account", user.getEmail(),
                "Thank you for signing up to Spring Reddit, " +
                        "please click on the below url to activate your account : " +
                        "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    public void verifyAccount(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new SpringRedditException("Token doesn't exist"));
        fetchUserByToken(verificationToken);
    }

    private void fetchUserByToken(VerificationToken verificationToken){
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found with username: " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest, HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        //response.addHeader(HttpHeaders.AUTHORIZATION, token);
        return new AuthenticationResponse(loginRequest.getUsername(), token);
    }

    public User getCurrentUser(){
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();
        return userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new SpringRedditException("User not found"));
    }

    public AuthenticationCheckResponse checkAuthentication(){
        boolean authenticated = false;
        AuthenticationCheckResponse authenticationCheckResponse = new AuthenticationCheckResponse();

        try {
            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                            .getAuthentication().getPrincipal();
            if(user.getUsername() != null){
                authenticated = true;
                User userFromRepo = getCurrentUser();
                authenticationCheckResponse.setUsername(userFromRepo.getUsername());
            } else {
                authenticationCheckResponse.setUsername(null);
            }
        } finally {
            authenticationCheckResponse.setAuthenticated(authenticated);
            return authenticationCheckResponse;
        }
    }

    public void logout(){
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
