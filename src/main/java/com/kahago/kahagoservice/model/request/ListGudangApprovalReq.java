package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author bangd ON 13/01/2020
 * @project com.kahago.kahagoservice.model.request
 */
@Data
public class ListGudangApprovalReq extends PageHeaderRequest {
    private String bookingCodeOrQrCode;
    private String officeCode;
    private Integer vendorCode;
    private String userIdSearch;
}
