package com.kahago.kahagoservice.model.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.model.request.DetailBooking;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value = Include.NON_NULL)
public class DetailTrxResponse {
	private String origin;
	private String destination;
	private String destinationCode;
	private String productCode;
	private String productName;
	private String senderName;
	private String senderAddress;
	private String senderTelp;
	private String senderEmail;
	private String senderSave;
	private String receiverName;
	private String receiverAddress;
	private String receiverTelp;
	private String receiverPostalCode;
	private String receiverIdPostalCode;
	private String receiverSave;
	private String receiverEmail;
	private Integer quantity;
	private String totalGrossWeight;
	private Long totalVolume;
	private String comodity;
	private String goodsId;
	private String serviceType;
	private String productType;
	private BigDecimal price;
	private BigDecimal priceGoods;
	private BigDecimal totalSurcharge;
	private BigDecimal totalInsurance;
	private BigDecimal totalExtraCharge;
	private String ppn;
	private BigDecimal totalPrice;
	private String officerId;
	private String trxDate;
	private String note;
	private String goodsDesc;
	@JsonProperty("receiver_kelurahan")
	private String kelurahan;
	@JsonProperty("receiver_kecamatan")
	private String kecamatan;
	@JsonProperty("receiver_kota")
	private String kota;
	@JsonProperty("receiver_provinsi")
	private String provinsi;
	private String bookingCode;
	private Double totalPackKg;
	@JsonProperty("resi_vendor")
	private String stt;
	private String qrcode;
	private String pickupAddressId;
	private String pickupTimeId;
	private String pickupDate;
	private String insuranceValue;
	private List<DetailBooking> detailBooking;
	private String vendor;
	private Integer status;
	private String statusDesc;
	private String productSwCode;
	private String urlResi;
	private Integer minWeight;
	private String jenisModa;
	private String namaModa;
	private BigDecimal priceKg;
	private Boolean isPack;
	private String discountCode;
	private String couponName;
	private BigDecimal discountValue;
	private String pembagiVolume;
	private String kgSurcharge;
	private String maxJumlahKoli;
	private String maxKgKoli;
	private String addressPickup;
	private String pickupTime;
	private String paymentOption;
	private String statusCode;
	private String noTiket;
	private String kodeUnik;
	private String imageUrl;
	private BigDecimal differenceAmount;
	private Boolean isBooking;
	private String originId;
	private String confirmResi;
	private Boolean isResiAuto;
}
