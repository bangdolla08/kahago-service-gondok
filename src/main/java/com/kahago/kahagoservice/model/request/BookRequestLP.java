package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@JsonSerialize
@Data
public class BookRequestLP {
	@JsonProperty("orderNumber")
    private String orderNumber;

    
    @JsonProperty("clientCode")
    private String clientCode;

    
    @JsonProperty("userType")
    private String userType;

    
    @JsonProperty("externalNumber")
    private String externalNumber;

    
    @JsonProperty("trackingNumber")
    private String trackingNumber;

    
    @JsonProperty("packageId")
    private String packageId;

    
    @JsonProperty("orderNumberTag")
    private String orderNumberTag;

    
    @JsonProperty("packageDate")
    private String packageDate;

    
    @JsonProperty("productType")
    private String productType;

    
    @JsonProperty("serviceType")
    private String serviceType;

    
    @JsonProperty("commodityType")
    private String commodityType;

    
    @JsonProperty("numberOfPieces")
    private String numberOfPieces;

    
    @JsonProperty("grossWeight")
    private String grossWeight;

    
    @JsonProperty("volumeWeight")
    private String volumeWeight;

    @JsonProperty("shipperName")
    private String shipperName;

    @JsonProperty("pickupAddress")
    private String pickupAddress;

    @JsonProperty("pickupLocation")
    private String pickupLocation;
    
    @JsonProperty("pickupPhone")
    private String pickupPhone;

    @JsonProperty("pickupEmail")
    private String pickupEmail;

    @JsonProperty("receiverName")
    private String receiverName;

    @JsonProperty("receiverAddress")
    private String receiverAddress;

    @JsonProperty("receiverLocation")
    private String receiverLocation;

    @JsonProperty("receiverPhone")
    private String receiverPhone;

    @JsonProperty("receiverEmail")
    private String receiverEmail;
}
