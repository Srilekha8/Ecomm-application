package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private String paymentMethod;
    private Long payGatewayPaymentId;
    private String payGatewayStatus;
    private String payGatewayRespMsg;
    private String payGatewayName;
}
