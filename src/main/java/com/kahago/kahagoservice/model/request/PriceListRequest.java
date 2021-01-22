package com.kahago.kahagoservice.model.request;
/**
 * @author Ibnu Wasis
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.sun.mail.imap.protocol.INTERNALDATE;
import lombok.Data;

@Data
@JsonSerialize
public class PriceListRequest extends PageHeaderRequest{
	private String AreaCode;
	private Integer switcherCode;
	private String search;
	private Integer statusVendorArea;
	private Integer typeSearch;
	private Boolean isCheck;
	private Integer idPostalCode;
}
