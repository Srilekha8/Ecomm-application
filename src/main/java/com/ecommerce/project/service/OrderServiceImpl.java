package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDto;
import com.ecommerce.project.payload.OrderItemDto;
import com.ecommerce.project.payload.OrderRequestDto;
import com.ecommerce.project.repository.*;
import jakarta.transaction.Transactional;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfChar;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.security.auth.RefreshFailedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private CartRepository cartRepository;

    AddressRepository addressRepository;

    PaymentRepository paymentRepository;

    OrderRepository orderRepository;

    OrderItemRepository orderItemRepository;

    ProductRepository productRepository;

    CartService cartService;

    ModelMapper modelMapper;



    public OrderServiceImpl(CartRepository cartRepository, AddressRepository addressRepository, PaymentRepository paymentRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartService cartService, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderDto placeOrder(String email, OrderRequestDto orderRequest, String payMethod) {
        //Get the user cart

        Cart cart = cartRepository.findCartByEmail(email);
        if (cart == null) {
            throw new APIException("cart not found with email " + email);
        }
        Address add = addressRepository.findById(orderRequest.getAddressId()).orElseThrow(
                () -> new ResourceNotFoundException("Address","AddressId",orderRequest.getAddressId())
        );
        //Place the order with payment info
        Order order = new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setOrderAmount(cart.getTotalPrice());
        order.setAddress(add);
        order.setOrderStatus("Order placed!");
        Payment payment = new Payment();
        payment.setPaymentMethod(payMethod);
        payment.setPaymentId(orderRequest.getPayGatewayPaymentId());
        payment.setPayGatewayStatus(orderRequest.getPayGatewayStatus());
        payment.setPayGatewayRespMsg(orderRequest.getPayGatewayRespMsg());
        payment.setPayGatewayName(orderRequest.getPayGatewayName());
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);
        Order savedOrder = orderRepository.save(order);
        //Add the user cart items into order items
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("cartItems is empty");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);
        //order.setOrderItems(orderItems);
        //Update the product stock
        cart.getCartItems().forEach(cartItem -> {
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            //clear the cart items
            cartService.deleteProdFromCart(product.getProductId());

        });

        //give the order summary as response

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        orderItems.forEach(orderItem ->
            orderDto.getOrderItemsList().add(
                    modelMapper.map(orderItem, OrderItemDto.class)
            ));
        orderDto.setAddressId(orderRequest.getAddressId());

        return orderDto;
    }
}
