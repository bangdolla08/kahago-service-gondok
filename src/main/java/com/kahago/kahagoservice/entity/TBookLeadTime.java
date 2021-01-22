package com.kahago.kahagoservice.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "t_book_leadtime")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TBookLeadTime {
	@GeneratedValue
	private Integer seqid;
	@Id
	@Column(name="booking_code")
	private String bookingCode;
	private Integer startDay;
	private Integer endDay;
}
