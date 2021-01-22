package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailPermohonan {
	private String bookId;
	private String invVendor;
	private String priceVendor;
	private String noPermohonan;
	private String statusPermohonan;
}
