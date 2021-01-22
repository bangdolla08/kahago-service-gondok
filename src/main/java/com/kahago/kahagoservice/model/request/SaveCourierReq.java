package com.kahago.kahagoservice.model.request;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author bangd ON 20/02/2020
 * @project com.kahago.kahagoservice.model.request
 */
@Data
public class SaveCourierReq {
    private String outgoingCode;
    private String courierId;
    private Boolean isPickup;
    private String courierName;
    private String phone;
    private String userId;
}
