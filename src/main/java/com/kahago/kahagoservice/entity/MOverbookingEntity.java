package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_overbooking")
@Data
public class MOverbookingEntity {
    @Id
    private Integer id;
    private String obNumber;
    private String startDate;
    private String endDate;
    private Integer status;
    private Long hppTotal;
    private Long packrepackTotal;
    private Long profitTotal;
    private Timestamp dateCreated;
    private Timestamp dateModified;
    private String userCreated;
    private String userModified;
}
