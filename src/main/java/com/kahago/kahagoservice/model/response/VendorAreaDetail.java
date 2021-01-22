package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(value = Include.NON_NULL)
public class VendorAreaDetail {
    private Integer postalCodeId;
    private Integer areaId;
    private String requestName;
    private String sendRequest;
    private Integer switcherCode;
    private Boolean isOrigin;
    private String switcherImage;
    private String title;
    private String tlc;
    private String longKelurahan;
    private Boolean isCheck;
    private Integer status;
    private String productName;
    private VendorArea vendorArea;
}
