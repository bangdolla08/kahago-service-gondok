package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OptionPaymentReq {
	private List<DetailBooks> books;
    private String priceGoods;
    private String note;
    private String paymentOption;
    private String discountCode;
    private String discountValue;
    private String userId;
    private String amount;
    private String phonePayment;
}
