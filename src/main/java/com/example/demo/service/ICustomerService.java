package com.example.demo.service;

import com.example.demo.dto.CustomerDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ICustomerService extends UserDetailsService {
    CustomerDto createCustomer(CustomerDto userDetails);

    CustomerDto getCustomerDetailsByEmail(String email);
}
