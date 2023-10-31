package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import com.example.demo.dto.CustomerDto;
import com.example.demo.service.ICustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final TokenUtil tokenUtil;
    private final ApplicationProperties applicationProperties;
    private final ICustomerService customerService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, TokenUtil tokenUtil,
                                ApplicationProperties applicationProperties, ICustomerService customerService) {
        super(authenticationManager);
        this.tokenUtil = tokenUtil;
        this.applicationProperties = applicationProperties;
        this.customerService = customerService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            AuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(),
                    AuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                    authenticationRequest.getPassword());
            return getAuthenticationManager().authenticate(authentication);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {
        String email = ((User) authResult.getPrincipal()).getUsername();
        CustomerDto customerDto = customerService.getCustomerDetailsByEmail(email);
        String token = tokenUtil.generateToken(customerDto.getCustomerId());
        response.addHeader(applicationProperties.getTokenHeader(), token);
        response.addHeader(applicationProperties.getCustomerIdHeader(), customerDto.getCustomerId());
        LOGGER.debug("Authentication is successful for customerId [{}].", customerDto.getCustomerId());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
        throw new UsernameNotFoundException(failed.getMessage(), failed);
    }
}

