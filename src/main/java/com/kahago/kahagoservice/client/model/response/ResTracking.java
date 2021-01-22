package com.kahago.kahagoservice.client.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import com.kahago.kahagoservice.model.response.TrackingPCP;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class ResTracking {
    private String rc;
    private String rd;
    private Boolean status;
    private String resi;
    private String courierCode;
    private List<Tracking> trackHistory;
    private List<TrackingPCP> detail;
}