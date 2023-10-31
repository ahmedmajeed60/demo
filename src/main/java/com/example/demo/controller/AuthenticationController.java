package com.example.demo.controller;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.ResponseBuilder;
import com.example.demo.service.IAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/login")
@RestController
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @RequestBody @Valid AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticateCustomer(authenticationRequest);
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(authenticationResponse));
    }
}
