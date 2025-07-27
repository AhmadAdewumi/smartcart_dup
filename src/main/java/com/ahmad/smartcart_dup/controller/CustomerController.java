package com.ahmad.smartcart_dup.controller;

import com.ahmad.smartcart_dup.dto.request.CreateCustomerRequest;
import com.ahmad.smartcart_dup.dto.response.ApiResponse;
import com.ahmad.smartcart_dup.model.Customer;
import com.ahmad.smartcart_dup.service.customer.ICustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(new ApiResponse("All customers retrieved!", customers));
    }

    @GetMapping("/id/{customerId}")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable long customerId) {
        Customer result = customerService.getCustomerById(customerId);
        CreateCustomerRequest toDto = customerService.convertToDto(result);
        return ResponseEntity.ok(new ApiResponse("Customer with id: " + customerId + ", retrieved successfully!", toDto));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCustomers(@RequestBody CreateCustomerRequest request) {
        customerService.createCustomer(request);
        return ResponseEntity.ok(new ApiResponse("Customer created successfully!", null));
    }

    @PatchMapping("/preferences/update")
    public ResponseEntity<ApiResponse> updatePreferences(long customerId, List<String> preferences) {
        Customer result = customerService.updatePreferences(customerId, preferences);
        CreateCustomerRequest toDto = customerService.convertToDto(result);
        return ResponseEntity.ok(new ApiResponse("Preferences updated successfully!", toDto));
    }

}
