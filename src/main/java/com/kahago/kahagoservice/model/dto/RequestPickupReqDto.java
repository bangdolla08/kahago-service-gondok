package com.kahago.kahagoservice.model.dto;

import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import lombok.Data;

/**
 * @author bangd ON 17/12/2019
 * @project com.kahago.kahagoservice.model.dto
 */
@Data
public class RequestPickupReqDto {
    private MUserEntity userEntity;
    private MPickupTimeEntity pickupTimeEntity;
    private TPickupAddressEntity pickupAddressEntity;
}
