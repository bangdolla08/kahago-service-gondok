package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class ProductSwitcherRequest {
	private Integer switcherCode;
	private Integer productSwCode;
	private String displayName;
	private String productVendorCode;
	private String liburStart;
	private String liburEnd;
	private String cutoff;
	private Integer jenisModa;
	private Boolean status;
	private Integer pembagiVolume;
	private Integer kgSurcharge;
	private Integer maxKoli;
	private Integer maxKgKoli;
	private Double pembulatanVolume;
	private Integer priority;
	private Integer komisi;
	private Boolean isNextRate;
	private Boolean isLeadTime;
	private Boolean isAutosync;
	private List<SurchargeDetailReq> surcharges;
}
