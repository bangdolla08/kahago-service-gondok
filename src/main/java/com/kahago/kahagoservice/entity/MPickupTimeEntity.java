package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "m_pickup_time")
@Data
public class MPickupTimeEntity {
    @Id
    private Integer idPickupTime;
    private LocalTime timeFrom;
    private LocalTime timeTo;
    private Integer isActive;
    private Integer currentDay;
    private LocalDateTime createdAt;
    private String createdBy;
}
