package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.enummodel.PaymentEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author bangd ON 28/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class BookDataResponse {
    private Integer seq;
    private String userId;
    private String userPhone;
    private String goodDesc;
    private String stt;
    private String bookingCode;
    private String shipperName;
    private String receiverName;
    private String pickupAddress;
    private String pickupPostalCode;
    private BigDecimal amount;
    private String receiverAddress;
    private String vendorUrlImage;
    private String vendorName;
    private String productName;
    private String statusCode;
    private String statusDesc;
    private String remainingTime;
    private String statusPay;
    private String status;
    private LocalDate dateTrx;
    private String timeTrx;
    private String origin;
    private String destination;
    private String dimension;
    private BigDecimal priceKg;
    private Integer qty;
    private Long weight;
    private Long volumeWeight;
    private String courierName;
    private Long pembagiVolume;
    private Double pembulatanVolume;
    private Boolean isInsurance;
    private Boolean isPack;
    private Double extraCharge;
    private Double insurance;
    private Double priceGoods;
    private Double totalPackKg;
    private String officeName;
    private Integer minWeight;
    private Double presentaseAsuransi;

    private String trxDate;
    private Boolean isBooking;
    private String qrcode;
    private String qrcodeExt;
    private String urlResi;
    private String urlResiVendor;
    private String senderPhone;
    private String senderAddress;
    private String receiverPhone;
    private String postalCode;
    private String paymentOption;
    private Integer totalWeight;
    private Integer shipSurcharge;
    private String discountCode;
    private Double discountValue;
    private Integer kodeUnik;
    private List<BookDetailResponse> detailBook;
    private List<SurchargeDetailResponse> surcharge;
    private ApprovalRejectWarehouseResponse approvalRejectWarehouseResponse;
    private PickupProperty pickup;
    private Integer idPostalCode;
}
