package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class UserCategoryResponse {
    private Integer id;
    private String nameCategory;
    private String accountType;
    private String roleName;
    private List<OptionPaymentResponse> optionPayment;
    private UserPriorityResponse userPriority;

    public void setAccountType(Integer accountType) {
        if (accountType == 1) {
            this.accountType = "internal";
        } else if (accountType == 2) {
            this.accountType = "external";
        }
    }
}
