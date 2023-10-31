package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenUtil {

    private final ApplicationProperties applicationProperties;

    public TokenUtil(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String generateToken(String subject) {
        String tokenSecret = applicationProperties.getSecretKey();
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(applicationProperties.getTokenExpiry())))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String getSubjectFromToken(String token) {
        String tokenSecret = applicationProperties.getSecretKey();
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        String customerId = claims.getSubject();
        Date expiry = claims.getExpiration();
        if (customerId != null && expiry != null && expiry.after(Date.from(Instant.now()))) {
            return customerId;
        }
        return null;
    }


}
