package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankDeposit {
    private Integer bankDepCode;
    private String bankCode;
    private String bankName;
    private String bankFullName;
    private String accountNo;
    private String accountName;
    private String imagePath;
    private Integer minimalTransaction;
    private Boolean isRobot;
}
