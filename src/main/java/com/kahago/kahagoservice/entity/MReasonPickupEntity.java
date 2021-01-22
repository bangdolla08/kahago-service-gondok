package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;

/**
 * @author Hendro yuwono
 */
@Entity
@Table(name = "m_reason_pickup")
@Data
public class MReasonPickupEntity {

    @Id
    private Integer id;
    private String description;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Integer active;
    private String createdBy;
    private LocalTime createdAt;
    private String lastUpdateBy;
    private LocalTime lastUpdateAt;

    public enum Category {
        CANCEL_IN_PICKUP, REJECT_IN_PICKUP
    }
}
