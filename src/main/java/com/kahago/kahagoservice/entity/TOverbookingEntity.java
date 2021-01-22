package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_overbooking")
@Data
public class TOverbookingEntity {
    @Id
    private Integer id;
    private Integer obId;
    private String bookingCode;
}
