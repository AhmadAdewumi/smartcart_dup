package com.ahmad.smartcart_dup.service.product;

import com.ahmad.smartcart_dup.dto.request.CreateCustomerRequest;
import com.ahmad.smartcart_dup.dto.request.CreateProductRequest;
import com.ahmad.smartcart_dup.model.Product;
import com.ahmad.smartcart_dup.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public ProductService(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product Not Found!"));
    }

    @Override
    public void createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setStockQuantity(request.getStockQuantity());
        product.setTags(request.getTags());
        product.setImageUrl(request.getImageUrl());
        product.setCreatedAt(request.getCreatedAt());

        productRepository.save(product);
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        return productRepository.findByCategory(category)
                .orElseThrow(() -> new RuntimeException(String.format("No Product Found with category: %s. ", category)));
    }

    @Override
    public CreateCustomerRequest convertToDto(Product product){
        return modelMapper.map(product, CreateCustomerRequest.class);
    }
}
