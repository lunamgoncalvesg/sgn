package com.sgn.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expMs;

    public JwtUtil(
            @Value("${sgn.jwt.secret}") String secret,
            @Value("${sgn.jwt.exp-ms}") long expMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMs = expMs;
    }

    public String gen(String mail, String legajo, String tipo) {
        var now = new Date();
        return Jwts.builder()
            .subject(mail)
            .claim("legajo", legajo)
            .claim("tipo", tipo)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expMs))
            .signWith(key)
            .compact();
    }

    public boolean valid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String mail(String token) {
        return parse(token).getSubject();
    }

    public String legajo(String token) {
        return parse(token).get("legajo", String.class);
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
