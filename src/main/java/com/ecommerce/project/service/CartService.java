package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartResponseDto;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;

import java.util.List;


public interface CartService {

    CartResponseDto addItemToCart(Long productId, Integer quantity);

    List<CartResponseDto> getAllCarts();

    CartResponseDto getUerCart();

    @Transactional
    CartResponseDto updateCartProductQuantity(Long productId, String quantity);

    @Transactional
    String deleteProdFromCart(Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
