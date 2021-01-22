package com.kahago.kahagoservice.client.model.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class Payment {
	@JsonProperty("bookingCode")
    private String bookingCode;
	@JsonProperty("pickupTimeId")
    private int pickupTimeId;
	@JsonProperty("userId")
    private String userId;
	@JsonProperty("officeCode")
    private String officeCode;
	@JsonProperty("trxDate")
    private String trxDate;
	@JsonProperty("trxTime")
    private String trxTime;
	@JsonProperty("productSwCode")
    private String productSwCode;
	@JsonProperty("senderTelp")
    private String senderTelp;
	@JsonProperty("receiverTelp")
    private String receiverTelp;
	@JsonProperty("amount")
    private String amount;
	@JsonProperty("trxServer")
    private String trxServer;
	@JsonProperty("reverseId")
    private String reverseId;
	@JsonProperty("reverseDate")
    private String reverseDate;
	@JsonProperty("adminTrx")
    private BigDecimal adminTrx;
	@JsonProperty("feeAdmin")
    private BigDecimal feeAdmin;
	@JsonProperty("feeSwitcher")
    private BigDecimal feeSwitcher;
	@JsonProperty("feeMitra")
    private BigDecimal feeMitra;
	@JsonProperty("feeInternal")
    private BigDecimal feeInternal;
	@JsonProperty("shippingSurcharge")
    private BigDecimal shippingSurcharge;
	@JsonProperty("jumlahLembar")
    private int jumlahLembar;
	@JsonProperty("datarekon")
    private String datarekon;
	@JsonProperty("productDstCode")
    private String productDstCode;
    private String status;
    private String stt;
    @JsonProperty("grossWeight")
    private int grossWeight;
    private int volume;
    private String origin;
    private String destination;
    @JsonProperty("senderName")
    private String senderName;
    @JsonProperty("senderEmail")
    private String senderEmail;
    @JsonProperty("senderAddress")
    private String senderAddress;
    @JsonProperty("receiverName")
    private String receiverName;
    @JsonProperty("receiverAddress")
    private String receiverAddress;
    @JsonProperty("receiverEmail")
    private String receiverEmail;
    @JsonProperty("idPostalCode")
    private String idPostalCode;
    @JsonProperty("serviceType")
    private String serviceType;
    private String comodity;
    private String note;
    @JsonProperty("goodsDesc")
    private String goodsDesc;
    @JsonProperty("priceGoods")
    private String priceGoods;
    private String resi;
    @JsonProperty("priceKg")
    private BigDecimal priceKg;
    private BigDecimal insurance;
    @JsonProperty("priceRepack")
    private BigDecimal priceRepack;
    @JsonProperty("totalHpp")
    private BigDecimal totalHpp;
    private BigDecimal profit;
    @JsonProperty("pvFlag")
    private String pvFlag;
    @JsonProperty("extraCharge")
    private BigDecimal extraCharge;
    @JsonProperty("pickupDate")
    private String pickupDate;
    @JsonProperty("pickupTime")
    private String pickupTime;
    @JsonProperty("noTiket")
    private String noTiket;
    @JsonProperty("pickupAddress")
    private String pickupAddress;
    private BigDecimal price;
    @JsonProperty("typePayment")
    private String typePayment;
    @JsonProperty("tenorPayment")
    private String tenorPayment;
    @JsonProperty("totalPackKg")
    private Integer totalPackKg;
    @JsonProperty("accessTokenInfo")
    private AccessTokenInfo accessTokenInfo;
    @JsonProperty("notifUrl")
    private String notifUrl;
    @JsonProperty("returnUrl")
    private String returnUrl;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("discountValue")
    private BigDecimal discountValue;
    @JsonProperty("discountCode")
    private String discountCode;
    @JsonProperty("idPayment")
    private String idPayment;
    @JsonProperty("idTicket")
    private String idTicket;
    @JsonProperty("paymentOption")
    private String paymentOption;
    @JsonProperty("insufficientFund")
    private BigDecimal insufficientFund;
    @JsonProperty("amountUniq")
    private BigDecimal amountUniq;
    @JsonProperty("amountDiff")
    private BigDecimal amountDiff;
    @JsonProperty("isConfirmTransfer")
    private String isConfirmTransfer;
}
