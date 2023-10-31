package com.example.demo.controller;

import com.example.demo.dto.CustomerDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.ResponseBuilder;
import com.example.demo.service.ICustomerService;
import com.example.demo.util.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize(Constant.HAS_ADMIN_ROLE)
    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(
            @RequestBody @Valid CustomerDto customerDto) {
        CustomerDto customerDtoResponse = customerService.createCustomer(customerDto);
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(customerDtoResponse));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize(Constant.HAS_ADMIN_OR_CLIENT_ROLE)
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByCustomerId(
            @PathVariable(name = "customerId") String customerId) {
        CustomerDto customerDtoResponse = customerService.getCustomerByCustomerId(customerId);
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(customerDtoResponse));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize(Constant.HAS_ADMIN_ROLE)
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @PathVariable(name = "customerId") String customerId,
            @RequestBody @Valid CustomerDto customerDto) {
        CustomerDto customerDtoResponse = customerService.updateCustomer(customerId, customerDto);
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse(customerDtoResponse));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize(Constant.HAS_ADMIN_ROLE)
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<String>> deactivateCustomer(
            @PathVariable(name = "customerId") String customerId) {
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.ok(ResponseBuilder.buildSuccessResponse("Customer is deactivated successfully!"));
    }
}
