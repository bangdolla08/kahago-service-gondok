package com.kahago.kahagoservice.client.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class TrackingLP {

    @JsonProperty("Date")
    private String date;
    @JsonProperty("STTNumber")
    private String sttNumber;
    @JsonProperty("TrackCode")
    private String trackCode;
    @JsonProperty("Piece")
    private String piece;
    @JsonProperty("Remarks")
    private String remarks;
    @JsonProperty("UpdatedBy")
    private String updatedBy;
    @JsonProperty("UpdatedOn")
    private String updatedOn;
}
