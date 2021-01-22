package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author bangd ON 20/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
public class ManifestListRequest extends PageHeaderRequest {
    private String driverId;
    private String manifestId;
    private List<Integer> status;
    private String pickupDate;
    private Integer idPickupTime;
}
