package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class ReportDailyRequest {
	private String startDate;
	private String endDate;
	private List<DetailStatus> status;
	private List<UserDetail> userId;
	private List<Vendor> vendor;
	private String format;
}
