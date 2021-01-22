package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class TrackRes {
    private Integer sequence;
    private String tanggal;
    private String stt;
    private String information;
    private String piece;
    private String bookingCode;
    private String shipmentBy;
    private String updatedBy;
    private String image;
}
