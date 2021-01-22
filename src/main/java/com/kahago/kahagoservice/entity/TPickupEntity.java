package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "t_pickup")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TPickupEntity {
	public TPickupEntity(TPickupEntity pickup) {
		this.idPickup = pickup.getIdPickup();
		this.courierId = pickup.getCourierId();
		this.timePickupId = pickup.getTimePickupId();
		this.code = pickup.getCode();
		this.pickupDate = pickup.getPickupDate();
		this.timePickupFrom = pickup.getTimePickupFrom();
		this.timePickupTo = pickup.getTimePickupTo();
		this.description = pickup.getDescription();
		this.status = pickup.getStatus();
		this.createBy = pickup.getCreateBy();
		this.createAt = pickup.getCreateAt();
		this.modifyBy = pickup.getModifyBy();
		this.modifyAt = pickup.getModifyAt();
		this.officeCode = pickup.getOfficeCode();
	}
    @Id
    @GeneratedValue
    private Integer idPickup;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "courierId",updatable=false)
    private MUserEntity courierId;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "timePickupId",updatable=false)
    private MPickupTimeEntity timePickupId;
    private String code;
    private LocalDate pickupDate;
    private LocalTime timePickupFrom;
    private LocalTime timePickupTo;
    private String description;
    private Integer status;
    private String createBy;
    private LocalDateTime createAt;
    private String modifyBy;
    private LocalDateTime modifyAt;
    private String officeCode;
//    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, orphanRemoval = true)
//    @JoinColumn(name = "pickupId")
//    private List<TPickupDetailEntity> entities;
}
