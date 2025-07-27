package com.ahmad.smartcart_dup.service.customer;

import com.ahmad.smartcart_dup.dto.request.CreateCustomerRequest;
import com.ahmad.smartcart_dup.model.Customer;
import com.ahmad.smartcart_dup.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService implements ICustomerService {
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    public CustomerService(ModelMapper modelMapper, CustomerRepository customerRepository) {
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() ->new RuntimeException("Customer Not Found!"));
    }

    @Override
    public void createCustomer(CreateCustomerRequest request) {
        if(customerRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email exists Already!");
        }
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPreferences(request.getPreferences());

        customerRepository.save(customer);
    }

    @Transactional
    @Override
    public Customer updatePreferences(long customerId, List<String> preferences) {
        return customerRepository.findById(customerId)
                .map(customer -> {
                    customer.setPreferences(preferences);
                    customerRepository.save(customer);
                    return customer;
                }).orElseThrow(() -> new RuntimeException("Customer Not Found!"));
    }

    @Override
    public CreateCustomerRequest convertToDto(Customer customer){
        return modelMapper.map(customer, CreateCustomerRequest.class);
    }
}
