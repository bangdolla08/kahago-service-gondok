package com.kahago.kahagoservice.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Entity
@Table(name="t_lead_time")
public class TLeadTimeEntity {
	@Id
	@GeneratedValue
	private Integer seqid;
	@OneToOne
	@JoinColumn(name="bookingCode")
	private TPaymentEntity bookingCode;
	private LocalDate trxDate;
	private String timeLeave;
	private String timeArrived;
	private String status;
	private Integer timeDifferent;
}
