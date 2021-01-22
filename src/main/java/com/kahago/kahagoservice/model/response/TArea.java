package com.kahago.kahagoservice.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TArea {
    private Integer seqid;
    private Integer vendor;
    private Integer areaId;
    private String areaSwitcher;
    private Boolean status;
    private String productSwitcher;
    private String tarif;
    private Integer startDay;
    private Integer endDay;
    private String lastUpdate;
    private String areaOriginId;
    private Integer minimumKg;
    private Integer limitMinimum;
    private String nextRate;
    private Integer productSwCode;
}
