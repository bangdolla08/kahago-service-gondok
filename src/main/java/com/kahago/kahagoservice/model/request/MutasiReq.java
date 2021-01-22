package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MutasiReq {
    @ApiModelProperty(value="User Id Login User",required = true)
    private String userId;
    @ApiModelProperty(value = "Start Date dengan parameter yang di butuhkan yyyyMMdd ex 20190219")
    private String startDate;
    @ApiModelProperty(value = "endDate Date dengan parameter yang di butuhkan yyyyMMdd ex 20190219")
    private String endDate;
    private String trxNo;
    private Integer trxType;
    private Pageable pageable;
}
