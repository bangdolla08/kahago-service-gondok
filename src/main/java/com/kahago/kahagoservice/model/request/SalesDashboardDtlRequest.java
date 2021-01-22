package com.kahago.kahagoservice.model.request;

import java.util.List;

import org.springframework.boot.context.properties.bind.DefaultValue;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class SalesDashboardDtlRequest extends PageHeaderRequest{
	private String key;
	private String statusUser;
	private String userSales;
	private Boolean past;
	private String officeCode;
	private List<Integer> idUserCategory;
	
}
