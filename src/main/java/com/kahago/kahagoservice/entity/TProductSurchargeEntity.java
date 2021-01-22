package com.kahago.kahagoservice.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Table(name="t_product_surcharge")
@Entity
@Data
public class TProductSurchargeEntity {
	@Id
	@GeneratedValue
	private Integer id;
	private Integer switcherCode;
	private Integer productSwCode;
	private Double percent;
	private Integer startKg;
	private Integer toKg;
	private Boolean status;
	private LocalDateTime createdDate;
	private String createdBy;
	private LocalDateTime updateDate;
	private String updateBy;
	
}
