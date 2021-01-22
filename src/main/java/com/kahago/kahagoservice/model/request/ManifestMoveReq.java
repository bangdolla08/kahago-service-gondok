package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
public class ManifestMoveReq {
	private List<String> books;
	private String codeManifestDestination;
	private String codeManifestOrigin;
	private String courierIdDestination;
}
