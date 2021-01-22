package com.kahago.kahagoservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Entity
@Table(name="m_user_priority")
@Data
public class MUserPriorityEntity {
	@Id
	@GeneratedValue
	private Integer seqid;
	private Integer userCategory;
	@Column(name="request_1")
	private Boolean request1;
	@Column(name="request_2")
	private Boolean request2;
	@Column(name="request_3")
	private Boolean request3;
	private Integer minKiriman;
	private Boolean paylater;
	private Boolean isResiAuto;
}
