package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class GoodsRes {
    private String id;
    private String goodsName;
    private Boolean insuranceFlag;
    private String insuranceValue;
    private Boolean packFlag;
    private String packValue;
    private String description;
    private Boolean status;
}
