package com.example.reddit.security;

import com.example.reddit.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {
    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/keys/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException exception){
            throw new SpringRedditException("Exception occured while loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        //PrivateKey privateKey = getPrivateKey();
        String token = Jwts.builder().setSubject(user.getUsername())
                .signWith(getPrivateKey())
                .compact();
        return token;
    }

    public boolean validateToken(String token){
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token);
        return true;
    }

    private PrivateKey getPrivateKey(){
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch(KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException exception){
            throw new SpringRedditException("Exception occured while retrieving the key from the store");
        }
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch(KeyStoreException e){
            throw new SpringRedditException("Exeption occured while retrieving the key from the store");
        }
    }

    public String getUsernameFromJwt(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
