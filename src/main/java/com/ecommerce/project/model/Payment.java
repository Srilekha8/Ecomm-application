package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Order order;

    @NotBlank
    @Size(min = 4, message = "Payement method must contain atleast 4 characters")
    private String paymentMethod;

    private Long payGatewayPaymentId;
    private String payGatewayStatus;
    private String payGatewayRespMsg;
    private String payGatewayName;

    public Payment(Long paymentId, Long payGatewayPaymentId, String payGatewayStatus, String payGatewayRespMsg, String payGatewayName) {
        this.paymentId = paymentId;
        this.payGatewayPaymentId = payGatewayPaymentId;
        this.payGatewayStatus = payGatewayStatus;
        this.payGatewayRespMsg = payGatewayRespMsg;
        this.payGatewayName = payGatewayName;
    }
}
