package com.kahago.kahagoservice.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author bangd ON 26/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
public class DepositRequest {
    private String paymentCode;
    private String paymentOption;
    private String userId;
    private String bankDepCode;
    private String bankCode;
    private String bankName;
    private String accountNo;
    private String accountName;
    private BigDecimal nominal;
    private String trxDate;
    private String tiketNo;
    private String description;
    private String typeTrx; //0. Deposit, 1.Payment, 2.Pickup
    private List<DetailBooks> books;
    private String phonePayment;
    private String date;
}
