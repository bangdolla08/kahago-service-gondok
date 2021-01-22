package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd ON 19/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PickupOrderListRequest extends PageHeaderRequest {
    @ApiModelProperty(value="User Id ")
    private String userId;
    @ApiModelProperty(value = "if you need an Order Date filter, please enter this variable")
    private String orderDate;
    @ApiModelProperty(value = "if you need an status filter, please enter this variable")
    private List<Integer> status;
    @ApiModelProperty(value = "if you need an Pickup Address/ created by, please enter this variable")
    private String filter;
    @ApiModelProperty(value = "if you need an Pickup Order Code/Qrcode Ext/Booking Code filter, please enter this variable")
    private String qrCode;
    
}
