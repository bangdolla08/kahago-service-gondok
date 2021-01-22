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
@Table(name="t_lead_time_history")
@Data
public class TLeadTimeHistoryEntity {
	@Id
	@GeneratedValue
	private Integer seqid;
	private String bookingCode;
	private String stt;
	private String description;
	private LocalDateTime trxDate;
	private LocalDateTime lastUpdate;
}
