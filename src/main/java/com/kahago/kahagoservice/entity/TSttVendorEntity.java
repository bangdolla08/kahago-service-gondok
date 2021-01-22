package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_stt_vendor")
@Data
public class TSttVendorEntity {
    @Id
    @GeneratedValue
    private Integer seq;
    private Integer switcherCode;
    private String stt;
    private Integer flag;
    private String origin;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
}
