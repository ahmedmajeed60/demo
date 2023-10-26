package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CustomerDto implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String customerId;
}
