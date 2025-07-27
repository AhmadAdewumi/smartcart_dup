package com.ahmad.smartcart_dup.controller;

import com.ahmad.smartcart_dup.dto.request.CreateProductRequest;
import com.ahmad.smartcart_dup.dto.response.ApiResponse;
import com.ahmad.smartcart_dup.model.Product;
import com.ahmad.smartcart_dup.service.aiService.AIRecommendationService;
import com.ahmad.smartcart_dup.service.aiService.AISearchService;
import com.ahmad.smartcart_dup.service.product.IProductService;
import com.ahmad.smartcart_dup.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final IProductService productService;
    private final AIRecommendationService aiRecommendationService;
    private final AISearchService aiSearchService;

    public ProductController(ProductService productService,
                             AIRecommendationService aiRecommendationService,
                             AISearchService aiSearchService) {
        this.productService = productService;
        this.aiRecommendationService = aiRecommendationService;
        this.aiSearchService = aiSearchService;
        logger.info("ProductController initialized with AI services");
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse("All customers retrieved!", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long productId) {
        logger.info("Fetching product with ID: {}", productId);
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(new ApiResponse("All customers retrieved!", product));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse>createProduct(@RequestBody CreateProductRequest request) {
        logger.info("Creating new product: {}", request.getName());
        productService.createProduct(request);
        logger.info("Product created with name: {}", request.getName());
        return ResponseEntity.ok(new ApiResponse("All customers retrieved!", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(@RequestParam String query) {
        logger.info("Searching products with query: '{}'", query);
        long startTime = System.currentTimeMillis();
        List<Product> results = aiSearchService.intelligentSearch(query);
        long endTime = System.currentTimeMillis();
        logger.info("Search completed in {}ms, found {} products", (endTime - startTime), results.size());
        return ResponseEntity.ok(new ApiResponse("Intelligent search completed!", results));
    }

    @GetMapping("/recommendations/{customerId}")
    public ResponseEntity<ApiResponse>getRecommendations(@PathVariable Long customerId) {
        logger.info("Fetching AI recommendations for customer ID: {}", customerId);
        long startTime = System.currentTimeMillis();
        List<Product> recommendations = aiRecommendationService.getPersonalizedRecommendations(customerId);
        long endTime = System.currentTimeMillis();
        logger.info("Recommendations generated in {}ms for customer: {}", (endTime - startTime), customerId);
        return ResponseEntity.ok(new ApiResponse("Customer recommendation generated retrieved!", recommendations));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable String category) {
        logger.info("Fetching products for category: {}", category);
        List<Product> products = productService.getProductByCategory(category);
        return ResponseEntity.ok(new ApiResponse("Products retrieved successfully!", products));

    }

    @PostMapping("/{productId}/generate-description")
    public ResponseEntity<ApiResponse> generateDescription(@PathVariable Long productId) {
        logger.info("Generating AI description for product ID: {}", productId);
        String result = aiRecommendationService.generateProductDescription(productId);
        return ResponseEntity.ok(new ApiResponse("Description generated successfully!", result));
    }


}
