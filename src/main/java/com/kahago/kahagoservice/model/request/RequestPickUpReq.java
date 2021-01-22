package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestPickUpReq {
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
