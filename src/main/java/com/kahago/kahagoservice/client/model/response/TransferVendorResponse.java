package com.kahago.kahagoservice.client.model.response;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class TransferVendorResponse {
	private String resultCode;
	private String resultMsg;
	private List<RespTransfer> resultData;
}
