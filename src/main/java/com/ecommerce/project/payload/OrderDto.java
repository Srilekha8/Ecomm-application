package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long orderId;
    private String email;
    private String mobileNumber;
    private List<OrderItemDto> orderItemsList;
    private LocalDate orderDate;
    private String orderStatus;
    private PaymentDto payment;
    private double totalAmount;
    private Long addressId;
}
