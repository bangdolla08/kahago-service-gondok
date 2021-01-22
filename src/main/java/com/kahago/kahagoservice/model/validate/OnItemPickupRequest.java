package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickCourierOnItemMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@PickCourierOnItemMustMatch
public class OnItemPickupRequest implements PickCourierOnItemMustMatch.ItemPickup {

    @NotNull
    private Integer id;

    @NotEmpty
    private String courierId;
}
