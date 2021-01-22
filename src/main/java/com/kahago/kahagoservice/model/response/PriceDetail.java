package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDetail {
    private String product;
    private String duration;
    private BigDecimal price;
    private String productCode;
    private String productName;
    private String shipmentType;
    private String serviceType;
    private Integer minWeight;
    private String cutOff;
    private String namaModa;
    private String urlImage;
    private Integer pembagiVolume;
    private Integer kgSurcharge;
    private Integer vendorCode;
    private Integer maxJumlahKoli;
    private Integer maxKgKoli;
    private Double pembulatanVolume;
    private Integer minKoli;
    private List<SurchargeDetailResponse> surcharge;
}
