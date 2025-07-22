package com.javaguy.smartcart.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @ElementCollection
    @CollectionTable(name = "customer_preferences", joinColumns = @JoinColumn(name = "customer_id"))
    private List<String> preferences = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "customer_purchase_history", joinColumns = @JoinColumn(name = "customer_id"))
    private List<Long> purchaseHistory = new ArrayList<>();

    private LocalDateTime createdAt;

    // Constructors
    public Customer() {}

    public Customer(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<String> getPreferences() { return preferences; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }

    public List<Long> getPurchaseHistory() { return purchaseHistory; }
    public void setPurchaseHistory(List<Long> purchaseHistory) { this.purchaseHistory = purchaseHistory; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt;
    }
}
