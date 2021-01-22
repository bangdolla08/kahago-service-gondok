package com.kahago.kahagoservice.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class PickupAddress {
    private Integer pickupId;
    private String courierId;
    private Integer pickupAddressId;
    private Integer statusPickup;
}
