package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Pageable;

/**
 * @author bangd ON 03/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class ReferenceRequest {
    private String userid;
    @ApiModelProperty(value = "Start Date dengan parameter yang di butuhkan yyyyMMdd ex 20190219")
    private String startDate;
    @ApiModelProperty(value = "Start Date dengan parameter yang di butuhkan yyyyMMdd ex 20190219")
    private String endDate;
    private Boolean active;
    private Pageable pageable;
}
