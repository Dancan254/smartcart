package com.javaguy.smartcart;

import com.javaguy.smartcart.entity.Customer;
import com.javaguy.smartcart.entity.Product;
import com.javaguy.smartcart.repository.CustomerRepository;
import com.javaguy.smartcart.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DataInitializer(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize sample products
        if (productRepository.count() == 0) {
            Product laptop = new Product("MacBook Pro", "High-performance laptop for professionals",
                    new BigDecimal("1299.99"), "Electronics", "Apple");
            laptop.setStockQuantity(50);
            laptop.setTags(Arrays.asList("laptop", "professional", "high-performance"));

            Product phone = new Product("iPhone 15", "Latest smartphone with advanced features",
                    new BigDecimal("899.99"), "Electronics", "Apple");
            phone.setStockQuantity(100);
            phone.setTags(Arrays.asList("smartphone", "mobile", "communication"));

            Product book = new Product("Spring Boot in Action", "Comprehensive guide to Spring Boot development",
                    new BigDecimal("45.99"), "Books", "Manning");
            book.setStockQuantity(30);
            book.setTags(Arrays.asList("programming", "java", "spring", "technical"));

            Product shoes = new Product("Running Shoes", "Comfortable athletic shoes for running",
                    new BigDecimal("129.99"), "Sports", "Nike");
            shoes.setStockQuantity(75);
            shoes.setTags(Arrays.asList("athletic", "running", "footwear"));

            productRepository.saveAll(Arrays.asList(laptop, phone, book, shoes));
        }

        // Initialize sample customers
        if (customerRepository.count() == 0) {
            Customer customer1 = new Customer("john.doe@example.com", "John", "Doe");
            customer1.setPreferences(Arrays.asList("Electronics", "Books", "Programming"));
            customer1.setPurchaseHistory(Arrays.asList(1L, 3L)); // Bought laptop and book

            Customer customer2 = new Customer("jane.smith@example.com", "Jane", "Smith");
            customer2.setPreferences(Arrays.asList("Sports", "Health", "Electronics"));
            customer2.setPurchaseHistory(Arrays.asList(2L, 4L)); // Bought phone and shoes

            customerRepository.saveAll(Arrays.asList(customer1, customer2));
        }
    }
}
