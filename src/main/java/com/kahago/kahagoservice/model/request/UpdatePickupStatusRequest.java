package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
public class UpdatePickupStatusRequest {
    private String status;
    private String reason;
}
