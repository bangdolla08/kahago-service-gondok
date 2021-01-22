package com.kahago.kahagoservice.client.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class ReqPayment {
	
    private String bookingCode;
    private String userId;
    private String stt;
    private String officeCode;
    private LocalDate trxDate;
    private String trxTime;
    private String productSwCode;
    private String senderTelp;
    private String receiverTelp;
    private BigDecimal amount;
    private BigDecimal priceGoods;
    private String resi;
    private String cacode;
    private Timestamp trxServer;
    private String rc;
    private String reverseId;
    private Timestamp reverseDate;
    private BigDecimal adminTrx;
    private BigDecimal feeAdmin;
    private BigDecimal feeSwitcher;
    private BigDecimal feeMitra;
    private BigDecimal feeInternal;
    private BigDecimal shippingSurcharge;
    private BigDecimal insurance;
    private BigDecimal extraCharge;
    private Integer jumlahLembar;
    private String datarekon;
    private String tglrekon;
    private String productDstCode;
    private Integer status;
    private String origin;
    private String destination;
    private Long grossWeight;
    private Long volume;
    private String comodity;
    private String note;
    private String goodsDesc;
    private String senderName;
    private String senderAddress;
    private String senderEmail;
    private String receiverName;
    private String receiverAddress;
    private String receiverEmail;
    private BigDecimal priceKg;
    private String pickupAddrId;
    private String idPostalCode;
    private String serviceType;
    private BigDecimal price;
    private BigDecimal priceRepack;
    private Double totalPackKg;
    private BigDecimal totalHpp;
    private BigDecimal profit;
    private String pickupTimeId;
    private LocalDate pickupDate;
    private String pickupTime;
    private String pvFlag;
    private String bankDepCode;
    private String noTiket;
    private String qrcode;
    private LocalDate qrcodeDate;
    private Integer goodsId;
    private String kantongPos;
    private BigDecimal htnbPos;
    private Byte mitraFlag;
    private String discountCode;
    private BigDecimal discountValue;
    private BigDecimal amountUniq;
    private BigDecimal amountDiff;
    private String resiPath;
    private String paymentOption;
    private String idPayment;
    private String idTicket;
    private BigDecimal insufficientFund;
    private Byte isConfirmTransfer;
    private Integer statusPay;
    private String qrcodeExt;
    @Transient
    private String tenorPayment;
    @Transient
    private String typePayment;
    @Transient
    private String phoneNumber;

}
