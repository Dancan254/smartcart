package com.javaguy.smartcart.controller;

import com.javaguy.smartcart.entity.Customer;
import com.javaguy.smartcart.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email address already in use");
        }
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<Customer> updatePreferences(
            @PathVariable Long id,
            @RequestBody List<String> preferences) {

        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setPreferences(preferences);
                    return ResponseEntity.ok(customerRepository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


