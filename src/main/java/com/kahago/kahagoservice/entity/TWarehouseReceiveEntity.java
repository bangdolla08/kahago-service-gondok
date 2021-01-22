package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "t_warehouse_receive")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TWarehouseReceiveEntity {

    @Id
    @GeneratedValue
    private Integer idWarehouseReceive;
    private Integer pickupId;
    private String code;
    private String officeCode;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
}
