package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author bangd ON 17/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Builder
@Data
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestPickupListResponse {
    private String customerName;
    private String customerId;
    private String customerAddress;
    private String phoneNumberCustomer;
    private String bookingCode;
    private String dimensionList;
    private Long wight;
    private Integer qtyItem;
    private String itemDescription;
    private String vendor;
}
