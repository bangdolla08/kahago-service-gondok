package com.kahago.kahagoservice.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Entity
@Table(name="t_category_pickup_time")
@Data
public class TCategoryPickupTimeEntity {
	@Id
	@GeneratedValue
	private Integer seqid;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name = "idUserCategory")
    private MUserCategoryEntity idUserCategory;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="idPickupTime")
	private MPickupTimeEntity idPickupTime;
	private Boolean actived;

}
