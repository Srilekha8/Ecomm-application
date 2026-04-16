package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CartResponseDto;
import com.ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponseDto> addItemtoCart(@PathVariable Long productId,
                                                         @PathVariable Integer quantity) {
        CartResponseDto cartResponseDto = cartService.addItemToCart(productId, quantity);
        return ResponseEntity.ok(cartResponseDto);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartResponseDto>> getCarts() {
        List<CartResponseDto> cartDto = cartService.getAllCarts();
        return ResponseEntity.ok(cartDto);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartResponseDto> getAllCarts() {
        CartResponseDto cartResponseDto = cartService.getUerCart();
        return ResponseEntity.ok(cartResponseDto);
    }

    @PutMapping("cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartResponseDto> updateProductQuantity(@PathVariable Long productId, @PathVariable String operation) {
        CartResponseDto message = cartService.updateCartProductQuantity(productId, operation);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteItemFromCart(@PathVariable Long productId) {
        String message = cartService.deleteProdFromCart(productId);
        return ResponseEntity.ok(message);
    }

}