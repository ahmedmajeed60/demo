package com.example.demo.service;

import com.example.demo.dto.CustomerDto;
import com.example.demo.entity.CustomerEntity;
import com.example.demo.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               BCryptPasswordEncoder bCryptPasswordEncoder,
                               ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomerEntity customerEntity = customerRepository.findByEmailAndActive(email, true);
        if (customerEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new org.springframework.security.core.userdetails.User(
                customerEntity.getEmail(), customerEntity.getPassword(), Collections.emptyList());
    }

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        customerDto.setCustomerId(UUID.randomUUID().toString());
        customerDto.setPassword(bCryptPasswordEncoder.encode(customerDto.getPassword()));
        CustomerEntity customerEntity = modelMapper.map(customerDto, CustomerEntity.class);
        customerRepository.save(customerEntity);
        LOGGER.debug("Customer with email [{}] is created successfully and assigned id [{}]",
                customerDto.getEmail(), customerDto.getCustomerId());
        return modelMapper.map(customerEntity, CustomerDto.class);
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) {
        CustomerEntity customerEntity = customerRepository.findByEmailAndActive(email, true);
        if (customerEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return modelMapper.map(customerEntity, CustomerDto.class);
    }

    @Override
    public CustomerDto getCustomerByCustomerId(String customerId) {
        CustomerEntity customerEntity = customerRepository.findByCustomerIdAndActive(customerId, true);
        if (customerEntity == null) {
            throw new IllegalArgumentException("Customer doesn't exist with customer id " + customerId);
        }
        return modelMapper.map(customerEntity, CustomerDto.class);
    }

    @Override
    public CustomerDto updateCustomer(String customerId, CustomerDto customerDto) {
        CustomerEntity customerEntity = customerRepository.findByCustomerIdAndActive(customerId, true);
        customerEntity.setEmail(customerDto.getEmail());
        customerEntity.setPassword(bCryptPasswordEncoder.encode(customerDto.getPassword()));
        customerEntity.setFirstName(customerDto.getFirstName());
        customerEntity.setLastName(customerDto.getLastName());
        customerEntity.setRole(customerDto.getRole());
        customerRepository.save(customerEntity);
        LOGGER.debug("Customer with email [{}] is updated successfully", customerDto.getEmail());
        return modelMapper.map(customerEntity, CustomerDto.class);
    }

    @Override
    public void deactivateCustomer(String customerId) {
        CustomerEntity customerEntity = customerRepository.findByCustomerIdAndActive(customerId, true);
        customerEntity.setActive(false);
        customerRepository.save(customerEntity);
    }
}
