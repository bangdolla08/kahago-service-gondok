package com.kahago.kahagoservice.model.response;

import lombok.Data;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
public class TransferResponse {
    private String uniqNumber;
    private String nominal;
    private String totalNominal;
    private String accountName;
    private String accountNo;
    private String imageBank;
    private String endTime;
    private String statusUniq;
    private String noTiket;
}
