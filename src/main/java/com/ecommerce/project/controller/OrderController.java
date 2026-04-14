package com.ecommerce.project.controller;

import com.ecommerce.project.payload.OrderDto;
import com.ecommerce.project.payload.OrderRequestDto;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.utility.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{payMethod}")
    private ResponseEntity<OrderDto> placeOrder(@RequestBody OrderRequestDto orderRequest, @PathVariable String payMethod){
        String email = authUtil.loggedInEmail();
        OrderDto orderResp = orderService.placeOrder(email,orderRequest,payMethod);
        return new ResponseEntity<>(orderResp, HttpStatus.CREATED);
    }
}
