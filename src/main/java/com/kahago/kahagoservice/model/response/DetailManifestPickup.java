package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kahago.kahagoservice.entity.TBookEntity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonInclude(value=Include.NON_NULL)
public class DetailManifestPickup {
	private Integer seq;
	private String customerName;
	private String customerTelp;
	private String address;
	private String kelurahan;
	private String kecamatan;
	private String addressNote;
	private String customerId;
	private Integer pickupAddressId;
	private String kota;
	private String postalCode;
	private String bookingCode;
	private Integer qty;
	private Long volume;
	private Long weight;
	private String isInsurance;
	private String pathImage;
	private String isPacking;
	private String pickupTime;
	private String pickupDate;
	private String qrcode;
	private String vendor;
	private String productSwCode;
	private String latitude;
	private String longitude;
	private String productVendor;
	private String imageVendor;
	private String flag;
	private String idPostalCode;
	private Integer status;
	private String statusDesc;
	private List<DimensiGoods> dimensi;
	private String lastUpdateManifestAt;
	private String lastUpdatePickupAt;
	private Integer pickupStatus;
	private Boolean isBooking;
	private String receiverName;
	private String receiverTelp;
	private String receiverAddress;
	private Boolean isFinish;
}
