package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "t_resp_transfer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TRespTransferEntity {
    @Id
    private Integer id;
    private Integer moduleId;
    private Long date;
    private String sender;
    private String note;
    private BigDecimal debit;
    private BigDecimal kredit;
    private String md5;
    private LocalDate lastUpdate;
    private Timestamp lastTime;
}
