package com.ahmad.smartcart_dup.service.customer;

import com.ahmad.smartcart_dup.dto.request.CreateCustomerRequest;
import com.ahmad.smartcart_dup.model.Customer;

import java.util.List;

public interface ICustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(long customerId);
    void createCustomer(CreateCustomerRequest request);
    Customer updatePreferences(long customerId, List<String> preferences);

    CreateCustomerRequest convertToDto(Customer customer);
}
