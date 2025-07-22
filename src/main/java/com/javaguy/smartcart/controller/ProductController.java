package com.javaguy.smartcart.controller;


import com.javaguy.smartcart.entity.Product;
import com.javaguy.smartcart.repository.ProductRepository;
import com.javaguy.smartcart.service.AIRecommendationService;
import com.javaguy.smartcart.service.AISearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;
    private final AIRecommendationService aiRecommendationService;
    private final AISearchService aiSearchService;

    public ProductController(ProductRepository productRepository,
                             AIRecommendationService aiRecommendationService,
                             AISearchService aiSearchService) {
        this.productRepository = productRepository;
        this.aiRecommendationService = aiRecommendationService;
        this.aiSearchService = aiSearchService;
        logger.info("ProductController initialized with AI services");
    }

    @GetMapping
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        logger.info("Retrieved {} products", products.size());
        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    logger.info("Found product: {}", product.getName());
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        logger.info("Creating new product: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        logger.info("Product created with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        logger.info("Searching products with query: '{}'", query);
        long startTime = System.currentTimeMillis();
        List<Product> results = aiSearchService.intelligentSearch(query);
        long endTime = System.currentTimeMillis();
        logger.info("Search completed in {}ms, found {} products", (endTime - startTime), results.size());
        return results;
    }

    @GetMapping("/recommendations/{customerId}")
    public List<Product> getRecommendations(@PathVariable Long customerId) {
        logger.info("Fetching AI recommendations for customer ID: {}", customerId);
        long startTime = System.currentTimeMillis();
        List<Product> recommendations = aiRecommendationService.getPersonalizedRecommendations(customerId);
        long endTime = System.currentTimeMillis();
        logger.info("Recommendations generated in {}ms for customer: {}", (endTime - startTime), customerId);
        return recommendations;
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        logger.info("Fetching products for category: {}", category);
        List<Product> products = productRepository.findByCategory(category);
        logger.info("Found {} products in category: {}", products.size(), category);
        return products;
    }

    @PostMapping("/{id}/generate-description")
    public ResponseEntity<String> generateDescription(@PathVariable Long id) {
        logger.info("Generating AI description for product ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    logger.info("Generating description for product: {}", product.getName());
                    long startTime = System.currentTimeMillis();
                    String description = aiRecommendationService.generateProductDescription(product);
                    long endTime = System.currentTimeMillis();
                    logger.info("Description generated in {}ms for product: {}", (endTime - startTime), product.getName());
                    return ResponseEntity.ok(description);
                })
                .orElseGet(() -> {
                    logger.warn("Cannot generate description - product not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}