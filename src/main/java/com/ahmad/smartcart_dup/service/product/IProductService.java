package com.ahmad.smartcart_dup.service.product;

import com.ahmad.smartcart_dup.dto.request.CreateCustomerRequest;
import com.ahmad.smartcart_dup.dto.request.CreateProductRequest;
import com.ahmad.smartcart_dup.model.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(long productId);
    void createProduct(CreateProductRequest product);
    List<Product> getProductByCategory(String category);

    CreateCustomerRequest convertToDto(Product product);
}
