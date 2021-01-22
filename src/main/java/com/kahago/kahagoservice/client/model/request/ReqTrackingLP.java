package com.kahago.kahagoservice.client.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class ReqTrackingLP {

    @JsonProperty("sttNumber")
    private String sttNumber;
}
