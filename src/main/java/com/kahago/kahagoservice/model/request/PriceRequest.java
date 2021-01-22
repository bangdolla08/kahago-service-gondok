package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd
 */
@Builder
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequest {
    @ApiModelProperty(value="Origin Price", required = true)
    @NotEmpty(message = "Please provide a origin Get From Origin list")
    private String origin;
    @ApiModelProperty(value="Destination Of Price Get From Kecamatan Id Get From URL /area/districts", required = true)
    @NotEmpty(message = "Please provide a Destination")
    private Integer destination;
    private Integer weight;
    @ApiModelProperty(value="Destination Of Price Get From Kecamatan Id Get From URL /goods")
    private Long commodityId;
    @ApiModelProperty(value="User Id if login data")
    private String userId;
}
