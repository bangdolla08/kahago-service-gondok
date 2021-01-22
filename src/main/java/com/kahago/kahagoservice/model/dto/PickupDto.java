package com.kahago.kahagoservice.model.dto;

import com.kahago.kahagoservice.entity.TPickupEntity;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 28/11/2019
 * @project com.kahago.kahagoservice.model.dto
 */
@Builder
@Data
public class PickupDto {
    private TPickupEntity pickupEntity;
    private Integer countProcessed;
    private Integer countQty;
}
