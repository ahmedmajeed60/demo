package com.example.demo.service;

public interface ITokenService {

    String generateToken(String subject);
    String getSubjectFromToken(String token);
}
