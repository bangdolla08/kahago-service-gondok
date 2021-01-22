package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class PermohononanListRespon {
	private String noPermohonan;
	private String trxDate;
	private Integer status;
	private String createUser;
	private String lastUser;
	private String createDate;
	private String lastUpdate;
	private List<PermohonanDetailResp> details;
}
