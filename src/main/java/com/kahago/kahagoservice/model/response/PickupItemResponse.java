package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PickupItemResponse extends PickupResponse {
    private Boolean firstTimeVisiting;
    private String statusDescription;
    private String reportManifest;
    private Long count;
    private List<Detail> details;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        private String idBook;
        private String sender;
        private String receiver;
        private String receiverAddress;
        private Integer totalItem;
        private Long weight;
        private String status;
        private String imageVendor;
        private Boolean isRequestPickup;
        private Boolean hasPieces;
        @JsonIgnore
        private String timePickup;
    }
}
