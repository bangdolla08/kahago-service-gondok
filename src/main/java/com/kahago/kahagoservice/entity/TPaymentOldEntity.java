package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "t_payment_old")
@Data
public class TPaymentOldEntity {
    @Id
    private String bookingCode;
    private String userId;
    private String stt;
    private String officeCode;
    private String trxDate;
    private String trxTime;
    private String productSwCode;
    private String senderTelp;
    private String receiverTelp;
    private Integer amount;
    private Integer priceGoods;
    private String resi;
    private String cacode;
    private Timestamp trxServer;
    private String rc;
    private String reverseId;
    private Timestamp reverseDate;
    private Integer adminTrx;
    private Integer feeAdmin;
    private Integer feeSwitcher;
    private Integer feeMitra;
    private Integer feeInternal;
    private Integer shippingSurcharge;
    private Integer insurance;
    private Integer extraCharge;
    private Integer jumlahLembar;
    private String datarekon;
    private String tglrekon;
    private String productDstCode;
    private Integer status;
    private String origin;
    private String destination;
    private Integer grossWeight;
    private Integer volume;
    private String comodity;
    private String note;
    private String goodsDesc;
    private String senderName;
    private String senderAddress;
    private String senderEmail;
    private String receiverName;
    private String receiverAddress;
    private String receiverEmail;
    private Integer priceKg;
    private Integer pickupAddrId;
    private Integer idPostalCode;
    private String serviceType;
    private Integer price;
    private Integer priceRepack;
    private Integer totalPackKg;
    private Integer totalHpp;
    private Integer profit;
    private Integer pickupTimeId;
    private Date pickupDate;
    private String pickupTime;
    private String pvFlag;
    private String bankDepCode;
    private String noTiket;
    private String qrcode;
    private Date qrcodeDate;
    private Integer goodsId;
    private String kantongPos;
    private Integer htnbPos;
    private Byte mitraFlag;
    private String discountCode;
    private String discountValue;
    private Integer amountUniq;
    private Integer amountDiff;
    private String resiPath;
    private String paymentOption;
    private String idPayment;
    private String idTicket;
    private Integer insufficientFund;
    private Byte isConfirmTransfer;
}
