package com.kahago.kahagoservice.model.response;

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
public class TbookDetailHistory {
	private String bookingCode;
	private String length;
	private String width;
	private String height;
	private String grossWeight;
	private String volumeWeight;
	private String counterChanges;
	
	private String lastLength;
	private String lastWidth;
	private String lastHeight;
	private String lastGrossWeight;
	private String lastVolumeWeight;
	
	private String differenceLength;
	private String differenceWidth;
	private String differenceHeight;
	private String differenceGrossWeight;
	private String differenceVolumeWeight;
}
