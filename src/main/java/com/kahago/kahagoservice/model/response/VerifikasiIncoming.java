package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifikasiIncoming {
	private String tag;
	private String title;
	@JsonProperty("idTrx")
	private String idTrx;
	private String tgl;
	private String userid;
	private String nominal;
	private String tipeTrx;
	private String statusTrx;
	private String body;
}
