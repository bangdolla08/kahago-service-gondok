package com.kahago.kahagoservice.client.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class Tracking {
    private String status;
    private String city;
    private String dateTime;
    private String receiverName;
    private String blStatus;
}
