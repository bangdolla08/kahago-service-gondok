package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author bangd ON 02/01/2020
 * @project com.kahago.kahagoservice.model.request
 */

@Data
@JsonSerialize
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListBookingCompleteReq extends PageHeaderRequest {
    private String searchString;
    private String date;
    private Integer pickupTimeId;
    private String officeCode;
}
