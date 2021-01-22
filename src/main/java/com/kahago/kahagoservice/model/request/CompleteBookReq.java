package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author bangd ON 02/01/2020
 * @project com.kahago.kahagoservice.model.request
 */

@Data
@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompleteBookReq {
    private String qrCode;
    private String userId;
    private Integer idPostalCode;
    private Long productCode;
    private String senderName;
    private String senderAddress;
    private String senderPhoneNumber;
    private String senderEmail;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhoneNumber;
    private String receiverEmail;
    private Long goodsId;
    private String description;
    private String instrutionSend;
    private Boolean isPackingKayu;
    private Boolean isAsuransi;
    private String goodsPrice;
    private String userAdmin;
    private Integer originId;
    private Integer idPickupTime;
    private String officeCode;
    private String qrCodeExt;
    private List<DetailBooking> detailBooking;
}
