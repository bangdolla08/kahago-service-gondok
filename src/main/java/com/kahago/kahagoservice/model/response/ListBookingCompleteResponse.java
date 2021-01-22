package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author bangd ON 02/01/2020
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListBookingCompleteResponse {
    private String orderId;
    private String userId;
    private String tglPickup;
    private String timePickup;
    private String vendor;
    private Integer jmlItem;
    private String qrCode;
    private String courierId;
    private String status;
    private String statusDec;
    private String address;
    private String kelurahan;
    private String kecamatan;
    private String kota;
    private String description;
    private String createBy;
    private String qrcodeExternal;
    private String bookingCode;
    private String statusRequest;
    private String statusBooking;
    private String produk;
}
