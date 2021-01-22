package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class ItemJne {
	@JsonProperty("customer_order_no")
    private String customerOrderNo;
    @JsonProperty("sender_telp")
    private String senderTelp;
    private String notes;
    private String weightVolume;
    private String weight;
    @JsonProperty("service_type")
    private String serviceType;
    @JsonProperty("destination_type")
    private String destinationType;
    @JsonProperty("destination_code")
    private String destinationCode;
    @JsonProperty("destination_area")
    private String destinationArea;
    @JsonProperty("destination_street")
    private String destinationStreet;
    @JsonProperty("destination_district")
    private String destinationDistrict;
    @JsonProperty("destination_city")
    private String destinationCity;
    @JsonProperty("destination_province")
    private String destinationProvince;
    @JsonProperty("destination_postcode")
    private String destinationPostcode;
    @JsonProperty("recipient_email_address")
    private String recipientEmailAddress;
    @JsonProperty("recipient_telp")
    private String recipientTelp;
    @JsonProperty("recipient_name")
    private String recipientName;
    @JsonProperty("parcel_price")
    private String parcelPrice;
    private String insurance;
    private String reseller;
    private String description;
    private String width;
    private String length;
    private String height;
}
