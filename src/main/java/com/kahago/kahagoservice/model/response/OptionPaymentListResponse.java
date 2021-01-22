package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionPaymentListResponse {
    private Integer paymentOptionId;
    private String paymentCode;
    private String paymentName;
    private String paymentImage;
    private String paymentImagePng;
    private String codeVendor;
    private String operatorVendor;
    private String usePhone;
    public List<BankDeposit> banks;
    private BigDecimal saldo;
    private BigDecimal minimalTransaction;
    private String urlpayment;
    private String urllogin;
    private String flagTrx;
    private String tiketTopup;
    
    //book
    private String urlResi;
    private String senderName;
    private String receiverName;
    private String bookingCode;
    private String flagSentOption;
    
    //bank transfer
    private String uniqNumber;
	private String nominal;
	private String totalNominal;
	private String accountName;
	private String accountNo;
	private String imageBank;
	private String endTime;
	private String statusUniq;
	private String noTiket;
	private String bankCode;
	private String bankName;
	private String screen;
}
