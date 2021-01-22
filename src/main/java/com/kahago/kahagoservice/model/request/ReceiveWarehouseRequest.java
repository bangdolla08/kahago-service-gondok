package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd ON 27/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
public class ReceiveWarehouseRequest extends PageHeaderRequest{
    @ApiModelProperty(value="User Id", required = true)
    @NotEmpty(message = "Please provide a userId Login")
    private String userId;
    @ApiModelProperty(value = "Office Code yang di dapatkan saat Login ",required = true)
    @NotEmpty(message = "Please provide a office Code")
    private String officeCode;
    @ApiModelProperty(value="Qr Code / New Qr Code Di Isi apabila masuk ke scan qrcode dan approve")
    @NotEmpty(message = "Please provide a Qr Code / New Qr Code")
    private String qrCode;
    
    @ApiModelProperty(value="Please insert booking Code")
    private String bookingCode;
    @ApiModelProperty(value="Please insert 3 digit phone number receiver")
    private String code;
    @ApiModelProperty(value="Please insert qrCodeExt")
    private String qrCodeExt;
    
    @ApiModelProperty(value="Kurir Id")
    private String courierId;
    
}
