package com.kahago.kahagoservice.model.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class ProductSwitcherResponse {
	private Integer productSwCode;
	private String productName;
	private String displayName;
	private String vendorName;
	private String Address;
	private String pic;
	private String phonePic;
	private String estimasi;
	private String tarif;
	private String minWeight;
	private String cutoff;
	private Boolean status;
	private String liburStart;
	private String liburEnd;
	private String serviceType;
	private String JenisModa;
	private String productVendorCode;
	private Integer priority;
	private String pembulatanVolume;
	private Integer maxKgKoli;
	private Integer maxKoli;
	private Integer pembagiVolume;
	private Integer kgSurcharge;
	private Integer komisi;
	private Boolean isNextRate;
	private Boolean isLeadTime;
	private String imageVendor;
	private Integer switcherCode;
	private Integer modaId;
	private Boolean isAutosync;
	private List<SurchargeDetailResponse> surcharges;
}
