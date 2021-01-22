package com.kahago.kahagoservice.model.response;

import java.util.List;

import lombok.Data;

@Data
public class ManifestListResp {
	private String courierId;
	private List<ManifestList> manifests;
}
