package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.model.response.PickupAddressResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bangd ON 18/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PickupAddressRequest {
    private String userId;
    private String address;
    private Integer idPostalCode;
    private Integer pickupAddressId;
    private String postalCode;
    private String description;
    private String longitude;
    private String latitude;
}
