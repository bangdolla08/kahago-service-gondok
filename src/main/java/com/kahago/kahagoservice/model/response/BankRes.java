package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class BankRes {
    private String bankCode;
    private String bankName;
    private String image;
}
