package com.example.demo.service;

import com.example.demo.dto.CustomerDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ICustomerService extends UserDetailsService {
    CustomerDto createCustomer(CustomerDto userDetails);

    CustomerDto getCustomerByEmail(String email);

    CustomerDto getCustomerByCustomerId(String customerId);

    CustomerDto updateCustomer(String customerId, CustomerDto customerDto);

    void deactivateCustomer(String customerId);
}
