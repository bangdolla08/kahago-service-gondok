package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bangd ON 29/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingResponse {
    private String codeOutgoing;
    private Integer vendorCode;
    private String vendorName;
    private String officeCode;
    private String officeName;
    private Integer sumVolume;
    private Integer qtyItem;
    private Integer sumWeight;
    private Boolean isEditable;
    private Integer statusOutgoing;
    private String statusOutgoingString;
    private String dateOutgoing;
    private Boolean isPickupVendor;
    private String courierName;
    private String courierPhone;
    private String courierId;
    private List<BookDataResponse> bookDataResponses;
    private String dateProcess;
}
