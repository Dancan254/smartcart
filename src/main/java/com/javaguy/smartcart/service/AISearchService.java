package com.javaguy.smartcart.service;

import com.javaguy.smartcart.entity.Product;
import com.javaguy.smartcart.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AISearchService {

    private static final Logger log = LoggerFactory.getLogger(AISearchService.class);
    private final ProductRepository productRepository;
    private final OllamaChatModel chatModel;
    public AISearchService(ProductRepository productRepository, OllamaChatModel chatModel) {
        this.productRepository = productRepository;
        this.chatModel = chatModel;
    }

    public List<Product> intelligentSearch(String userQuery){
        //we first try using traditional search
        List<Product> traditionalResults = productRepository.findBySearchQuery(userQuery);
        log.info("Traditional search returned {} results", traditionalResults.size());
        if (!traditionalResults.isEmpty()){
            log.info("Traditional search returned {} results", traditionalResults.size());
            return traditionalResults;
        }
        //what if traditional one fails? hehe, we got backup
        log.info("Traditional search failed, trying enhanced search");
        String enhancedQuery = enhanceSearchQuery(userQuery);
        log.info("Enhanced query: '{}'", enhancedQuery);
        //lets search again
        List<Product> enhancedResults = productRepository.findBySearchQuery(enhancedQuery);
        log.info("Enhanced search returned {} results", enhancedResults.size());
        if (!enhancedResults.isEmpty()){
            log.info("Enhanced search successful, returning {} products", enhancedResults.size());
            return enhancedResults;
        }
        log.info("Enhanced search failed, trying performance semantic search");
        // fall back plan
        return performanceSemanticSearch(userQuery);
    }
    private List<Product> performanceSemanticSearch(String userQuery){
        List<Product> allProducts = productRepository.findAll();

        String aiResponse = chatModel.call("The user is searching for products with the query: '" + userQuery + "'\n" +
                "Suggest 3-5 alternative keywords or categories that might match their intent. " +
                "Focus on product categories, brands, or features. Return only the keywords separated by commas.");
        return allProducts.stream()
                .filter(product -> {
                    String response = aiResponse.toLowerCase();
                    return response.contains(product.getCategory().toLowerCase()) || response.contains(product.getName().toLowerCase());
                })
                .limit(5)
                .toList();
    }

    private String enhanceSearchQuery(String originalQuery) {
        String prompt = "The user is searching for products with the query: '" + originalQuery + "'\n" +
                "Suggest 3-5 alternative keywords or categories that might match their intent. " +
                "Focus on product categories, brands, or features. Return only the keywords separated by commas.";

        return chatModel.call(prompt);
    }
}
