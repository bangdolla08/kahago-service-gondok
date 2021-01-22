package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 06/01/2020
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class PickupOrderDetail {
    private Integer areaId;
    private String receiverName;
    private Integer qty;
    private Double weight;
    private String productSwCode;
    private Integer switcherCode;
    private String bookingNumber;
    private String qrCode;
    private String userId;
    private Integer originId;
    private PriceDetail price;
}
