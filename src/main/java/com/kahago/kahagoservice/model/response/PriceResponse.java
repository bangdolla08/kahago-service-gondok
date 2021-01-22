package com.kahago.kahagoservice.model.response;

import lombok.Data;

import java.util.List;

@Data
public class PriceResponse {
    private List<PriceDetail> prices;
    private List<VendorDetail> vendors;
}
