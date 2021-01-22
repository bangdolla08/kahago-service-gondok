package com.kahago.kahagoservice.model.request;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class MasterUserRequest extends PageHeaderRequest {
	
	@Email
	@NotBlank
	private String userId;
	
	private List<Integer> userCategoryId;
	private String reference;
	private Integer userType;
	private String officeCode;
	private String search;
	private String name;
	private String phoneNo;
	private String password;
	private Integer userCategory;
	private Integer creditDay;
	private Boolean isCourier;
	private String accountNo;
	
}
