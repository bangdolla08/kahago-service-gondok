package com.kahago.kahagoservice.model.dto;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bangd ON 01/12/2019
 * @project com.kahago.kahagoservice.model.dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private TPaymentEntity paymentEntity;
    private PaymentEnum paymentEnum;
    private String bookCode;
}
