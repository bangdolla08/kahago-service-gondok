package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 05/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@JsonSerialize
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DepositResponse {
    private String noTiket;
    private String bankCode;
    private String bankName;
    private String accountNo;
    private String accountName;
    private String screen;
    private String trxDate;
    private String uniqNumber;
    private String totalNominal;
    private String endTime;
    private String statusUniq;
    private String nominal;
    private String imageBank;
    private String userId;
    private String statusDesc;
    private Integer status;
    private String userPhone;
    private String description;
}
