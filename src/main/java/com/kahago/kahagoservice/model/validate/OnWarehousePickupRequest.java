package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickCourierOnWarehouseMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@PickCourierOnWarehouseMustMatch
public class OnWarehousePickupRequest implements PickCourierOnWarehouseMustMatch.PickupStatusOnWarehouse {

    @NotEmpty
    private String courierId;

    @NotEmpty
    private String status;

}
