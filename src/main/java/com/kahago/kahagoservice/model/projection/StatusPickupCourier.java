package com.kahago.kahagoservice.model.projection;

import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
public class StatusPickupCourier {
    private Integer idPickupDetail;
    private Integer pickupAddrId;
    private String statusPickup;

    public StatusPickupCourier(int idPickupDetail, int pickupAddrId, String statusPickup) {
        this.idPickupDetail = idPickupDetail;
        this.pickupAddrId = pickupAddrId;
        this.statusPickup = statusPickup;
    }

    public StatusPickupCourier() {
    }
}
