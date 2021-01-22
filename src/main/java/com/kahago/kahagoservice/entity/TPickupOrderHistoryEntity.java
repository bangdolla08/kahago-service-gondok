package com.kahago.kahagoservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Entity
@Table(name="t_pickup_order_history")
@Data
public class TPickupOrderHistoryEntity {
	@Id
	@GeneratedValue
	private Integer id;
	private String pickupOrderId;
	private Integer pickupOrderDetailId;
	private Integer status;
	private Integer lastStatus;
	private String createdBy;
	private LocalDateTime createdDate;
	private String reason;
}
