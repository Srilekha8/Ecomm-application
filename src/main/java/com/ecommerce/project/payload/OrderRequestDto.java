package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private Long addressId;
    private Long paymentMethod;
    private Long payGatewayPaymentId;
    private String payGatewayStatus;
    private String payGatewayRespMsg;
    private String payGatewayName;
}
