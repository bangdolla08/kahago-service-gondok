package com.kahago.kahagoservice.client.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class ResTrackingLP {
    private String rc;
    private String rd;
    @JsonProperty("ShipmentTravelHistory")
    private List<TrackingLP> shipmentTrackingLP;
}
