package com.kahago.kahagoservice.model.response;

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
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
@Builder
public class ApprovalRejectWarehouseResponse {
	private Integer idWarehouseDetail;
	private String userId;
	private String noManifest;
	private String destination;
	private String bookId;
	private List<String> dimensi;
	private String volumeWeight;
	private String weight;
	private String newVolumeWeight;
	private String newWeight;
	private String vendorName;
	private String productName;
	private Integer status;
	private String statusDesc;
	private String urlImage;
	private String branch;
	private String volume;
	private String newVolume;
	private Long priceDifference;
	private List<String> dimensiHistory;
}
