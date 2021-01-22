package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
public class UnfinishedBookListReq {
    private String userId;
    private String officeCode;
}
