package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Setter
@Getter
public class AuthenticationResponse implements Serializable {

    String token;
}
