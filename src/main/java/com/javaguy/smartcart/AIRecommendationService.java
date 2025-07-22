package com.javaguy.smartcart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(AIRecommendationService.class);
    private final OllamaChatModel chatModel;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public AIRecommendationService(OllamaChatModel chatModel, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.chatModel = chatModel;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        log.info("AIRecommendationService initialized with ollama chat model and product repository");
    }

    public List<Product> getPersonalizedRecommendations(long customerId){
        log.info("Generating personalized recommendations for customer with id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        //get the products they have purchased
        List<Product> purchasedProducts = customer.getPurchaseHistory().stream()
                .map(productRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        log.info("Customer has {} purchased products: {}", purchasedProducts.size(),
                purchasedProducts.stream().map(Product::getName).toList());
        // generate ai powered recommendations
        String recommendationPrompt = createRecommendationPrompt(customer, purchasedProducts);
        log.debug("Recommendation prompt: {}", recommendationPrompt);
        String aiResponse = chatModel.call(recommendationPrompt);
        return parseRecommendationsAndFindProducts(aiResponse);
    }

    private String createRecommendationPrompt(Customer customer, List<Product> purchasedProducts) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(("Based on the following customer profile and purchase history, recommend product categories and types that would interest them:\n"));
        prompt.append("Customer: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
        prompt.append("Preferences: ").append(String.join(", ", customer.getPreferences())).append("\n");
        if (!purchasedProducts.isEmpty()){
            prompt.append("Recent purchases:\n");
            purchasedProducts.forEach(product ->
                    prompt.append("_ ").append(product.getName())
                                    .append(" (").append(product.getCategory())
                                    .append("\n"));

        }
        prompt.append("""
                \nPlease suggest 3-5 product categories or specific product types 
                that would complement their interests. Focus on categories like:
                Electronics, Books, Toys, Fashion, Grocery, Beauty, Clothing, Sports
                """);

        return prompt.toString();

    }

    private List<Product> parseRecommendationsAndFindProducts(String aiResponse){
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(product -> {
                    String response = aiResponse.toLowerCase();
                    return response.contains(product.getCategory().toLowerCase()) || response.contains(product.getName().toLowerCase());
                })
                .limit(5)
                .toList();
    }

    public String generateProductDescription(Product product){
        log.info("Generating product description for product with id: {}", product.getId());
        PromptTemplate promptTemplate = new PromptTemplate(
                "Generate an engaging, SEO-friendly product description for the following product:\n" +
                        "Name: {name}\n" +
                        "Category: {category}\n" +
                        "Brand: {brand}\n" +
                        "Current Description: {description}\n\n" +
                        "Make it compelling and highlight key features and benefits."
        );
        Map<String, Object> promptVariables = Map.of(
                "name", product.getName(),
                "category", product.getCategory(),
                "description", product.getDescription(),
                "brand", product.getBrand()
        );

        Prompt prompt = promptTemplate.create(promptVariables);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}

