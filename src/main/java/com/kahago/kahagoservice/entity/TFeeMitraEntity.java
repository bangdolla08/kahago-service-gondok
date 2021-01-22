package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_fee_mitra")
@Data
public class TFeeMitraEntity {
    @Id
    private String bookingCode;
    private String payMonth;
    private String caCode;
    private String status;
    private String createdUser;
    private Timestamp createdDate;
    private String descr;
}
