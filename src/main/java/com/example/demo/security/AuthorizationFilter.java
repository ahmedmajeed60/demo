package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import com.example.demo.util.Constant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    private final ApplicationProperties applicationProperties;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               ApplicationProperties applicationProperties) {
        super(authenticationManager);
        this.applicationProperties = applicationProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String authorizationHeader = req.getHeader(applicationProperties.getAuthorizationHeader());
        if (authorizationHeader == null || !authorizationHeader.startsWith(Constant.BEARER)) {
            LOGGER.debug("[{}] header is not present.", applicationProperties.getAuthorizationHeader());
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
        String token = authorizationHeader.replace(Constant.BEARER, "");
        String tokenSecret = applicationProperties.getSecretKey();
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        String customerId = Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
        if (customerId == null) {
            throw new UsernameNotFoundException("Authorization is failed due to invalid token " + token);
        }
        LOGGER.debug("Authorization is successful for customerId [{}].", customerId);
        return new UsernamePasswordAuthenticationToken(customerId, null, new ArrayList<>());

    }


}
