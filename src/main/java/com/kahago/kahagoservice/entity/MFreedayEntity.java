package com.kahago.kahagoservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Entity
@Table(name="m_freeday")
@Data
public class MFreedayEntity {
	@Id
	@GeneratedValue
	private Integer seqid;
	private String dayName;
	private int isActive;
	private String description;
}
