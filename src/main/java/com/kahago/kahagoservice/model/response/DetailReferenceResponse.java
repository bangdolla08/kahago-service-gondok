package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bangd ON 04/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Builder
@Data
public class DetailReferenceResponse {
    private ProfileRes profileRes;
    private BigDecimal depositPertama;
    private BigDecimal depositLanjutan;
    private BigDecimal countBook;
    private BigDecimal sumBook;
    private BigDecimal feeDepositPertama;
    private BigDecimal feeDepositLanjutan;
    private BigDecimal feeBook;
    private BigDecimal totalFee;
}
