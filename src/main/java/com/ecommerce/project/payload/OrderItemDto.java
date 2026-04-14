package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long orderItemId;
    private ProductRequestDto product;
    private Integer quantity;
    private double discount;
    private double orderedItemPrice;
}
