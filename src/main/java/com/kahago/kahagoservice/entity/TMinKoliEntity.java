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
@Table(name="t_min_koli")
@Data
public class TMinKoliEntity {
	@Id
	@GeneratedValue
	private Long seqid;
	private Long productSwCode;
	private Integer idUserCategory;
	private Integer minKoli;

}
