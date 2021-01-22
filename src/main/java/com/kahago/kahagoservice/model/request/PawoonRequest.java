package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class PawoonRequest {
	private List<String> books;
	@JsonProperty("user_id")
	private String userid;
	private String hit;
}
