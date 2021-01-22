package com.kahago.kahagoservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bangd ON 02/12/2019
 * @project com.kahago.kahagoservice.model.dto
 */
@Data
@Builder
public class DepositMailDto {
    private String userId;
    private BigDecimal nominal;
    private String bankName;
    private String noAccount;
    private String nameAccount;
    private String ket;
    private String urlBoc;
}
