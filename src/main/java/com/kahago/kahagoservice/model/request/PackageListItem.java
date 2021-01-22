package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class PackageListItem {
	@JsonProperty("receipt_number")
    private String receiptNumber;

    @JsonProperty("cust_package_id")
    private String custPackageId;

    @JsonProperty("origin_code")
    private String originCode;

    @JsonProperty("delivery_type")
    private String deliveryType;

    @JsonProperty("parcel_category")
    private String parcelCategory;

    @JsonProperty("parcel_content")
    private String parcelContent;

    @JsonProperty("parcel_qty")
    private String parcelQty;

    @JsonProperty("parcel_uom")
    private String parcelUom;

    @JsonProperty("parcel_value")
    private String parcelValue;

    @JsonProperty("cod_value")
    private String codValue;

    @JsonProperty("total_weight")
    private String totalWeight;

    @JsonProperty("parcel_length")
    private String parcelLength;

    @JsonProperty("parcel_width")
    private String parcelWidth;

    @JsonProperty("parcel_height")
    private String parcelHeight;

    @JsonProperty("shipper_name")
    private String shipperName;

    @JsonProperty("shipper_address")
    private String shipperAddress;

    @JsonProperty("shipper_province")
    private String shipperProvince;

    @JsonProperty("shipper_city")
    private String shipperCity;

    @JsonProperty("shipper_district")
    private String shipperDistrict;

    @JsonProperty("shipper_zip")
    private String shipperZip;

	@JsonProperty("shipper_phone")
	private String shipperPhone;

    @JsonProperty("shipper_code")
    private String shipperCode;

    @JsonProperty("recipient_title")
    private String recipientTitle;

    @JsonProperty("recipient_name")
    private String recipientName;

	@JsonProperty("recipient_address")
	private String recipientAddress;

    @JsonProperty("recipient_province")
    private String recipientProvince;

	@JsonProperty("recipient_city")
	private String recipientCity;

    @JsonProperty("recipient_district")
    private String recipientDistrict;

    @JsonProperty("recipient_zip")
    private String recipientZip;

	@JsonProperty("recipient_phone")
	private String recipientPhone;

    @JsonProperty("destination_code")
    private String destinationCode;

    @JsonProperty("notes")
    private String notes;
}
