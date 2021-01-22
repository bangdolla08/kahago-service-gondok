package com.kahago.kahagoservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Entity
@Table(name="t_map_layanan")
@Data
public class TMapLayananEntity {
	@Id
	private Integer seqid;
	private Integer idPostalCode;
	private Integer switcherCode;
	private Integer idModa;
}
