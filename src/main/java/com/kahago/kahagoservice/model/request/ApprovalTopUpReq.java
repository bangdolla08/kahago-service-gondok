package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class ApprovalTopUpReq {
	private Integer status;
	private String bankId;
	private String tiketNo;
	private String userAdmin;
}
