package com.ahmad.smartcart_dup.service.aiService;

import com.ahmad.smartcart_dup.model.Product;
import com.ahmad.smartcart_dup.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AISearchService {
    private final ChatModel chatModel;
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(AISearchService.class);

    public AISearchService(ChatModel chatModel, ProductRepository productRepository) {
        this.chatModel = chatModel;
        this.productRepository = productRepository;
    }

    public List<Product> intelligentSearch(String query) {
        log.info("About to perform intelligent search for query: {}", query);
        List<Product> traditionalSearch = productRepository.findBySearchQuery(query);
        if (!traditionalSearch.isEmpty()) {
            log.info("Traditional search not empty and retrieved, {} products!", traditionalSearch.size());
            return traditionalSearch;
        }
        log.info("Traditional search failed, moving on to enhanced search!");

        // what if  traditional search fails, then we enhance the query by letting an llm generate related words for it
        String enhancedQuery = enhanceSearchQuery(query);

        List<String> enhancedQueryList = Arrays.asList(enhancedQuery.split(","));
        log.info("Query converted to list: {} ", enhancedQueryList);

        // now , we perform traditional search on each query
        List<Product> enhancedSearchList = new ArrayList<>();
        for (String eachQuery : enhancedQueryList) {
            List<Product> result = productRepository.findBySearchQuery(eachQuery);
            enhancedSearchList.addAll(result);
        }
        if (!enhancedSearchList.isEmpty()) {
            log.info("Enhanced search successful");
            return enhancedSearchList;
        }

        // Fallback if the two approach above fails
        log.info("Enhanced search query failed! Moving to performance semantic search");
        return performanceSemanticSearch(query);
    }

    //fallback
    private List<Product> performanceSemanticSearch(String query) {
        List<Product> allProducts = productRepository.findAll();
        String aiResponse = getAIResponse(query);
        return allProducts.stream().filter(product -> {
            String response = aiResponse.toLowerCase();
            return response.contains(product.getName().toLowerCase()) || response.contains(product.getCategory().toLowerCase());
        }).limit(5).toList();
    }

    private String enhanceSearchQuery(String originalQuery) {
        String prompt = "The user is searching for products with the query: '" + originalQuery + "'\n" +
                "Suggest 3-5 alternative keywords or categories that might match their intent. " +
                "Focus on product categories, brands, or features. Return only the keywords separated by commas.";

        return chatModel.call(prompt);
    }

    private String getAIResponse(String originalQuery) {
        String prompt = "The user is searching for products with the query: '" + originalQuery + "'\n" +
                "Suggest 3-5 alternative keywords or categories that might match their intent. " +
                "Focus on product categories, brands, or features. Return only the keywords separated by commas.";
        return chatModel.call(prompt);
    }
}
