package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VendorArea {
    private Integer postalCodeId;
    private String postalCode;
    private String province;
    private Integer provinceId;
    private String city;
    private Integer cityId;
    private String kecamatan;
    private Integer kecamatanId;
    private String kelurahan;
    private String tlc;
    private String title;
    private List<VendorAreaDetail> vendorAreaDetail;
}
