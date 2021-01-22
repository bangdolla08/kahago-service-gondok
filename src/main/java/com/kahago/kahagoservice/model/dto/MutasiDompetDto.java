package com.kahago.kahagoservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kahago.kahagoservice.enummodel.MutasiEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.model.dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutasiDompetDto {
    private String trxNo;
    private LocalDate trxDate;
    private LocalTime trxTime;
    private String trxLocalDateTime;
    private String descr;
    private String debet;
    private String kredit;
    private BigDecimal amount;
    private String userId;
    private String productSwCode;
    private BigDecimal sisaSaldo;
    private Integer trxType;
    private MutasiEnum mutasiEnum;
}
