package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bangd ON 06/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
public class ReportTransactionRes {
    private String name;
    private String userId;
    private String kota;
    private String phoneNumber;
    private BigDecimal depositPertama;
    private BigDecimal depositLanjutan;
    private Integer totalBook;
    private BigDecimal penjualan;
    private BigDecimal feeDepositPertama;
    private BigDecimal feeDepositLanjutan;
    private BigDecimal feePenjualan;
}
