package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRespDto {

    private Long cartItemId;
    private Integer quantity;
    private Double discount;
    private ProductResponseDto product;
    //private CartResponseDto cart;
    private Double productPrice;
}
