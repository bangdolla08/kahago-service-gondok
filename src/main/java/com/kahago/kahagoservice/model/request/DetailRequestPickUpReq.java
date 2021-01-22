package com.kahago.kahagoservice.model.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DetailRequestPickUpReq {
    private Integer areaDetailId;
    private Double weight;
    private Integer qtyItem;
    private Long productSwitcherCode;
    private String receiverName;
    private Integer isPay;
}
