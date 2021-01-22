package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bangd ON 03/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserList {
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean haveDetail;
	private String city;
	private String depositPertama;
	private String feeDepositPertama;
	private String depositLanjutan;
	private String feeDepositLanjutan;
	private String totalBooking;
	private String penjualan;
	private String feePenjualan;

}
