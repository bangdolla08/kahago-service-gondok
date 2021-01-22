package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author BangDolla08
 * @created 07/10/20-October-2020 @at 11.25
 * @project kahago-service
 */
@Data
@JsonSerialize
public class SttVendorReq {
    private Integer idSttVendor;
    private Integer switcherCode;
    private String stt;
    private String origin;
    private Integer flag;
}
