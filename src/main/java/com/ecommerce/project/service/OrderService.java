package com.ecommerce.project.service;

import com.ecommerce.project.payload.OrderDto;
import com.ecommerce.project.payload.OrderRequestDto;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDto placeOrder(String email, OrderRequestDto orderRequest, String payMethod);
}
