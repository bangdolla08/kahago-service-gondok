package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class ApprovalReport {

    private String noTicket;
    private String bankCode;
    private String bankName;
    private String accountNo;
    private String accountName;
    private String dateOfTransaction;
    private String uniqueNumber;
    private String nominal;
    private String userId;
    private String status;
    private String userPhoneNumber;
    private String processBy;
    private String dateOfApproval;
    private String description;
}
