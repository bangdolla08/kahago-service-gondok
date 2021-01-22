package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.model.request.DetailRequestPickUpReq;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 14 Jan 2020
 */
@Builder
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class RequestPickUpResp {
	private String pickupOrderId;
    private String userId;
    private Integer pickupAddressID;
    private Integer pickupTimeId;
    @ApiModelProperty(value = "Start Date dengan parameter yang di butuhkan yyyyMMdd ex 20190219")
    private String pickupDate;
    private Integer qtyItem;
    @ApiModelProperty(value="Apabila user menggunakan metode bayar sekarang di isi dengan 1 apabila bayar nanti di isi 0",example = "0")
    private Integer paymentType;
    private BigDecimal totalPay;
    private List<DetailRequestPickUpReq> detail;
    
}
