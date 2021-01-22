package com.kahago.kahagoservice.model.response;
/**
 * @author Ibnu Wasis
 */

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TrackingPCP {
	@JsonProperty("AwbNo")
	private String awbNo;
	@JsonProperty("TrackingDate")
    private String trackingDate;
    @JsonProperty("OfficeName")
    private String officeName;
    @JsonProperty("StatusId")
    private Integer statusId;
    @JsonProperty("StatusName")
    private String statusName;
    @JsonProperty("Reason")
    private String reason;
}
