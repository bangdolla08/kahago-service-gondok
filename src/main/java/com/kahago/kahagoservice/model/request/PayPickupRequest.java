package com.kahago.kahagoservice.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author bangd ON 26/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
public class PayPickupRequest {
    private String paymentOption;
    private String userId;
    private BigDecimal nominal;
    private String trxDate;
    private String pickupOrderId;
    private String description;
    private String typeTrx; //0. Deposit, 1.Payment, 2.Request Pickup
    private String phonePayment;
    private List<String> qrcodes;
}
