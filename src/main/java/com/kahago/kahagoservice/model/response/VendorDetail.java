package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorDetail {
    private Integer vendorCode;
    private String vendorName;
}
