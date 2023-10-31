package com.example.demo.service;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;

public interface IAuthenticationService {

    AuthenticationResponse authenticateCustomer(AuthenticationRequest authenticationRequest);
}
