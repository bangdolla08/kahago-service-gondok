package com.kahago.kahagoservice.model.request;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class BookRequestJNE {
	private String bookCode;
    private String origin;
    private String destination;
    private String productCode;
    private String senderName;
    private String senderAddress;
    private String senderPhone;
    private String senderEmail;
    private String senderCity;
    private String senderPostalCode;
    private String senderRegion;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String receiverEmail;
    private String receiverPostalCode;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverProvince;
    private String receiverIsland;
    private String receiverSubDistrict;
    private String goodsDescription;
    private String notes;
    private String goodsPrice;
    private String modaId;
    private String totalItem;
    private List<ItemJne> items;
    private String insurance;
    private String price;
    private String apiKey;
    private String partnerId;
    private String branch;
}
