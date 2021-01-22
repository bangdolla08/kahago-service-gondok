package com.kahago.kahagoservice.model.request;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 19 Nov 2019
 */
@Data
@Builder
@AllArgsConstructor
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BookRequest {
	@NotEmpty
	private String origin;
	@NotEmpty
	private String destination;
	@NotEmpty
	private String productCode;
	@NotEmpty
	private String senderName;
	@NotEmpty
	@Size(min=1, max=100)
	private String senderAddress;
	@NotEmpty
	@Size(min=1, max=20)
	private String senderTelp;
	private String senderEmail;
	private String senderSave;
	@NotEmpty
	private String receiverName;
	@NotEmpty
	@Size(min=1, max=100)
	private String receiverAddress;
	@NotEmpty
	@Size(min=1, max=20)
	private String receiverTelp;
	@NotEmpty
	@JsonProperty("receiver_id_postal_code")
	private String receiverPostalCode;
	@NotEmpty
	private String receiverSave;
	private String receiverEmail;
	@NotEmpty
	private String quantity;
	@NotEmpty
	@Size(min=1)
	private String totalGrossWeight;
	@NotEmpty
	@Size(min=1)
	private String totalVolume;
	@NotNull
	private Long comodity;
	private String serviceType;
	private String productType;
	@NotEmpty
	private String price;
	private String priceGoods;
	private String totalSurcharge;
	private String totalInsurance;
	private String totalPackingPrice;
	@NotEmpty
	private String ppn;
	@NotEmpty
	private String totalPrice;
	@Email
	private String userId;
	private String trxDate;
	private String note;
	private String goodsDescription;
	@NotEmpty
	private String pickupId;
	private String totalPackKg;
	@NotNull
	private Integer idPickupTime;
	@NotEmpty
	private String pickupDate;
	@NotEmpty
	private String payType;
	private Integer minWeight;
	private Boolean isCounter;
	private String officeCode;
	private String qrcodeExt;
	@NotNull
	private List<DetailBooking> detailBooking;
	private String bookingCode;
	public BookRequest() {
		this.priceGoods = "0";
		this.note = " ";
	}
}
