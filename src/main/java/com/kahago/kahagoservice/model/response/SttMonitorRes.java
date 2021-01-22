package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author BangDolla08
 * @created 06/10/20-October-2020 @at 16.20
 * @project kahago-service
 */
@Data
@Builder
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SttMonitorRes {
    private Integer vendorCode;
    private String vendorName;
    private String vendorImg;
    private String originData;
    private BigInteger countingData;
}
