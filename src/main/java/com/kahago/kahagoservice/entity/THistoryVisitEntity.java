package com.kahago.kahagoservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Entity
@Table(name="t_history_visit")
public class THistoryVisitEntity {
	@Id
	@GeneratedValue
	private Integer seqid;
	private String url;
	private String param;
	private String action;
	private String userId;
	private Integer flag;
}
