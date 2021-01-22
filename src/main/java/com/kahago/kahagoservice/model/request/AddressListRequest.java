package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd ON 20/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddressListRequest {
    @ApiModelProperty(value="User Id", required = true)
    @NotEmpty(message = "Please provide a userId Login")
    private String userId;
    @ApiModelProperty(value="Destination Id Just Put in Destination Id From getProductSwitcher Price To getProductSwitcher reliable Recever Address")
    private Integer destinationId;
    @ApiModelProperty(value="untuk mengambil data nya getUser")
    private String userIdAndAccountNo;
}
