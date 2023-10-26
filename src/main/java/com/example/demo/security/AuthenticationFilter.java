package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final IUserService userService;
    private final ApplicationProperties applicationProperties;

    public AuthenticationFilter(IUserService userService, AuthenticationManager authenticationManager,
                                ApplicationProperties applicationProperties) {
        super(authenticationManager);
        this.userService = userService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            AuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), AuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()
            );
            return getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {
        String userName = ((UserEntity) authResult.getPrincipal()).getEmail();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);
        String tokenSecret = applicationProperties.getSecretKey();
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(userDetails.getUserId())
                .expiration(Date.from(now.plusMillis(applicationProperties.getTokenExpiry())))
                .issuedAt(Date.from(now))
                .signWith(secretKey, Jwts.SIG.HS512).compact();
        response.addHeader(applicationProperties.getTokenHeader(), token);
        response.addHeader(applicationProperties.getUserIdHeader(), userDetails.getUserId());
    }
}

