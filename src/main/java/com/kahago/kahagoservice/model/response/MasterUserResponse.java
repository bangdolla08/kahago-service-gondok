package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class MasterUserResponse{
	private String userId;
	private String accountNo;
	private String name;
	private String userCategory;
	private Integer idUserCategory;
	private String telpNo;
	private String userReference;
	private String userType;
	private Integer idUserType;
	private String officeCode;
	private String branchName;
	private Boolean courierFlag;
	private Integer creditDay;
}
