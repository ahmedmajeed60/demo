package com.example.demo.controller;

import com.example.demo.dto.CustomerDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.ResponseBuilder;
import com.example.demo.service.ICustomerService;
import com.example.demo.util.Constant;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/customer")
@RestController
public class CustomerController {

    private final ICustomerService customerService;

    @Autowired
    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize(Constant.HAS_ADMIN_ROLE)
    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@RequestBody @Valid CustomerDto customerDto) {
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(customerService.createCustomer(customerDto)));
    }

    @PreAuthorize(Constant.HAS_ADMIN_OR_CLIENT_ROLE)
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByCustomerId(
            @PathVariable(name = "customerId") String customerId) {
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(
                customerService.getCustomerByCustomerId(customerId)));
    }

    @PreAuthorize(Constant.HAS_ADMIN_ROLE)
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @PathVariable(name = "customerId") String customerId,
            @RequestBody @Valid CustomerDto customerDto) {
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(
                customerService.updateCustomer(customerId, customerDto)));
    }
}
