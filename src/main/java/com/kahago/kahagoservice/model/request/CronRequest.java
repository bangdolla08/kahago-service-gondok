package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.model.response.TArea;
import com.kahago.kahagoservice.model.response.VendorArea;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(value = Include.NON_NULL)
@JsonSerialize
public class CronRequest {
	private List<TArea> areas;
	private List<VendorArea> vendorAreas;
}
