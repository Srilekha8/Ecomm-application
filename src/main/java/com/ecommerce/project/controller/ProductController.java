package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.ProductRequestDto;
import com.ecommerce.project.payload.ProductResponseDto;
import com.ecommerce.project.service.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    private ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDto> getproducts(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNum,
                                                          @RequestParam(name= "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                          @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                          @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        ProductResponseDto products = productService.getAllProducts(pageNum,pageSize, sortBy, sortOrder);
        return ResponseEntity.ok().body(products);

    }
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDto> getProductsByCategory(@PathVariable Long categoryId,
                                                                    @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNum,
                                                                    @RequestParam(name= "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                    @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                    @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        ProductResponseDto products = productService.getProductsByCategory(categoryId, pageNum,pageSize, sortBy, sortOrder);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponseDto> getProductByKeyword(@PathVariable String keyword,
                                                                  @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNum,
                                                                  @RequestParam(name= "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                  @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                  @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        ProductResponseDto productsBykey = productService.getProductsByKeyword(keyword,pageNum,pageSize, sortBy, sortOrder);
        return ResponseEntity.ok().body(productsBykey);

    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductRequestDto> addProduct(@Valid @RequestBody ProductRequestDto product,
                                                        @PathVariable Long categoryId) {
        ProductRequestDto savedProduct = productService.addProduct(categoryId, product);
        return ResponseEntity.ok().body(savedProduct);

    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductRequestDto> updateProduct(@Valid @RequestBody ProductRequestDto product, @PathVariable Long productId) {
        ProductRequestDto updatedProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok().body(updatedProduct);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String response = productService.deleteProduct(productId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductRequestDto>  updateProductImage(@PathVariable Long productId,
                                                                 @RequestParam MultipartFile productImage) throws IOException {
        ProductRequestDto savedImageProduct = productService.saveOrUpdateProductImage(productImage, productId);
        return new ResponseEntity<>(savedImageProduct, HttpStatus.OK);
    }
}
