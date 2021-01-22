package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ProductSwitcher {
    private Integer productSwCode;
    private Integer switcherCode;
    private String name;
    private String displayName;
    private String operatorSw;
    private String status;
    private Integer startDay;
    private Integer endDay;
    private String lastUser;
    private Timestamp lastUpdate;
    private Integer tarif;
    private String serviceType;
    private String minWeight;
    private String cutoff;
    private Integer komisi;
    private Integer jenisModa;
    private String liburStart;
    private String liburEnd;
    private Integer autosync;
}
