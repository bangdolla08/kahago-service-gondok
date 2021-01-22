package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddressRequest {
    @ApiModelProperty(value="Receiver/Sender Id Address If edit")
    private Integer id;
    @ApiModelProperty(value="Receiver/Sender Name")
    private String name;
    @ApiModelProperty(value="Receiver/Sender address")
    private String address;
    @ApiModelProperty(value="Receiver/Sender Telephone")
    private String telp;
    @ApiModelProperty(value="Postal Code Id Please provide a Postal Code untuk receiver Address Harus Di isi")
    private Integer idPostalCode;
    @ApiModelProperty(value="Email")
    private String email;
    @ApiModelProperty(value="User Id", required = true)
    @NotEmpty(message = "Please provide a userId Login")
    private String userId;
}
