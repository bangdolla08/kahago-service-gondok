package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class PickupItemDetailResponse {
    private int partId;
    private String bookingCode;
    private String dimension;
    private String grossWeight;
    private String status;
}
