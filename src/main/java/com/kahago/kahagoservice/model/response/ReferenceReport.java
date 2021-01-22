package com.kahago.kahagoservice.model.response;
/**
 * @author Ibnu Wasis
 */

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ReferenceReport {
	private String accountNo;
	private String userId;
	private String userName;
	private String totalDepositPertama;
	private String totalFeeDepositPertama;
	private String totalDepositLanjutan;
	private String totalFeeDepositLanjutan;
	private String totalBooking;
	private String totalPenjualan;
	private String totalFeePenjualan;
	private Page<UserList> detail;
}
