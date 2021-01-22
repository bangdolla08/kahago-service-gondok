package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 17/12/2019
 * @project com.kahago.kahagoservice.model.response
 */

@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FooterReceiveWarehouseResponse {
    private String courierName;
    private String pickupTime;
    private Integer barangBelomDiterima;
    private Integer pesananBelomDiterima;
}
