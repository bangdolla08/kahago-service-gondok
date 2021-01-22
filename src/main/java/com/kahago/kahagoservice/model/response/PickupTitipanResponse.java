package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@SuperBuilder
public class PickupTitipanResponse {
    private int totalItem;
    private String nameOfAccount;
    private String phone;
    private String reportManifest;
    private List<TimePickup> pickups;

    @Data
    @SuperBuilder
    public static class TimePickup {
        private String pickupTime;
        private List<PickupItemResponse.Detail> item;
    }
}
