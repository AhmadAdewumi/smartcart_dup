package com.ahmad.smartcart_dup.service.aiService;

import com.ahmad.smartcart_dup.model.Customer;
import com.ahmad.smartcart_dup.model.Product;
import com.ahmad.smartcart_dup.repository.CustomerRepository;
import com.ahmad.smartcart_dup.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIRecommendationService {
    private static final Logger log = LoggerFactory.getLogger(AIRecommendationService.class);
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ChatModel chatModel;

    public AIRecommendationService(CustomerRepository customerRepository, ProductRepository productRepository, ChatModel chatModel) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.chatModel = chatModel;
    }

    public List<Product> getPersonalizedRecommendations(long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException(String.format("Customer with id, %d, Not Found!", customerId)));

        List<Product> purchasedProducts = customer
                .getPurchaseHistory()
                .stream()
                .map(productRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        String recommendationPrompt = createRecommendationPrompt(customer, purchasedProducts);

        String aiResponse = chatModel.call(recommendationPrompt);
        return parseRecommendationsAndFindProducts(aiResponse);
    }


    private String createRecommendationPrompt(Customer customer, List<Product> purchasedProducts) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(("Based on the following customer profile and purchase history, recommend product categories and types that would interest them:\n"));
        prompt.append("Customer: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
        prompt.append("Preferences: ").append(String.join(", ", customer.getPreferences())).append("\n");
        if (!purchasedProducts.isEmpty()) {
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

    public List<Product> parseRecommendationsAndFindProducts(String aiResponse) {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(product -> {
                    String response = aiResponse.toLowerCase();
                    return response.contains(product.getCategory().toLowerCase()) || response.contains(product.getName().toLowerCase());
                })
                .limit(5)
                .toList();
    }

    public String generateProductDescription(long productId) {
        log.info("Fetching product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found!"));
        log.info("Product fetched successfully with name: {}", product.getName());
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
                "brand", product.getBrand(),
                "description", product.getDescription()
        );

        Prompt prompt = promptTemplate.create(promptVariables);
        return chatModel.call(prompt).getResult().getOutput().getText();

    }
}
