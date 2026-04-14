package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductRequestDto;
import com.ecommerce.project.payload.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService{

    ProductResponseDto getProductsByCategory(Long  categoryId, Integer pageNum, Integer pageSize, String sortBy, String sortOrder);

    ProductRequestDto addProduct(Long categoryId, ProductRequestDto product);

    ProductResponseDto getProductsByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortBy, String sortOrder);

    ProductRequestDto updateProduct(Long productId, ProductRequestDto product);

    String deleteProduct(Long productId);

    ProductRequestDto saveOrUpdateProductImage(MultipartFile productImage, Long productId) throws IOException;

    ProductResponseDto getAllProducts(Integer pageNum, Integer pageSize, String sortBy, String sortOrder);
}
