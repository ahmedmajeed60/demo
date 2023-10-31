package com.example.demo.service.impl;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.CustomerDto;
import com.example.demo.service.IAuthenticationService;
import com.example.demo.service.ICustomerService;
import com.example.demo.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final ICustomerService customerService;
    private final ITokenService tokenService;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     ICustomerService customerService,
                                     ITokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticationResponse authenticateCustomer(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
        CustomerDto customerDto = customerService.getCustomerByEmail(authenticationRequest.getEmail());
        String token = tokenService.generateToken(customerDto.getCustomerId());
        return new AuthenticationResponse(token);
    }
}
