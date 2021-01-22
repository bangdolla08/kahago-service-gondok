package com.kahago.kahagoservice.client.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTransfer {
	private String username;
	private String password;
	private String startdate;
	private String enddate;
	private String configid;
}
