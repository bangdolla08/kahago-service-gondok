package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author bangd ON 29/11/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OutgoingRequest extends PageHeaderRequest{
    @ApiModelProperty(value="User Approve", required = true)
    @NotEmpty(message = "Please provide a origin Get From Origin list")
    private String userId;
    @ApiModelProperty(value="Vendor Code Di isi Saat Create Dan Check Barang Maupun Saat Confim")
    private Integer vendorCode;
    @ApiModelProperty(value="Qr Code Di isi Saat Create Dan Check Barang Maupun Saat Confim")
    private String qrCode;
    @ApiModelProperty(value="office Di isi Saat Create Dan Check Barang Maupun Saat Confim")
    private String officeCode;
    @ApiModelProperty(value="If need Show detail Item and add Or Save")
    private String manifestId;
    @ApiModelProperty(value="Filter Status outgoing ( All, Belum Print=0, Belum Upload=1,Upload=2)")
    private Integer status;
    @ApiModelProperty(value="Filter outgoing number")
    private String outgoingNumber;
    @ApiModelProperty(value="Booking Code Transaksi")
    private String bookingCode;
    
}
