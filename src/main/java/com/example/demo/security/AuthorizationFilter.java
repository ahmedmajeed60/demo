package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private static final String BEARER = "Bearer";
    private final ApplicationProperties applicationProperties;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               ApplicationProperties applicationProperties) {
        super(authenticationManager);
        this.applicationProperties = applicationProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String authorizationHeader = req.getHeader(applicationProperties.getAuthorizationHeader());

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(applicationProperties.getAuthorizationHeader());
        if (authorizationHeader == null) {
            return null;
        }
        String token = authorizationHeader.replace(BEARER, "");
        String tokenSecret = applicationProperties.getSecretKey();
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        String userId = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        if (userId == null) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

    }


}
