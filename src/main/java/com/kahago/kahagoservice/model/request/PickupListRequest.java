package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd ON 19/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PickupListRequest {
    @ApiModelProperty(value="User Id From user Login", required = true)
    @NotEmpty(message = "Please provide a user Id")
    private String userId;
    @ApiModelProperty(value = "if you need an Origin filter, please enter this variable")
    private String origin;
}
