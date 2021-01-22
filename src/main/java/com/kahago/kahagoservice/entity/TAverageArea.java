package com.kahago.kahagoservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 6 Jul 2020
 */
@Entity
@Table(name = "t_area_average")
@Data
public class TAverageArea {
	@Id
	@GeneratedValue
	private int seqid;
	@OneToOne
	@JoinColumn(name = "seqidArea")
	private TAreaEntity area;
	private BigDecimal averageArea;
	private Boolean isCheck;
	private LocalDateTime lastUpdate;
}
