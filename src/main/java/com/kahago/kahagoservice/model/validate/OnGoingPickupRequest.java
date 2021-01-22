package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickCourierOnGoingMustMatch;
import com.kahago.kahagoservice.validation.PickCourierOnReadyMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@PickCourierOnGoingMustMatch
public class OnGoingPickupRequest implements PickCourierOnGoingMustMatch.PickupStatusOnGoing {
    @NotEmpty
    private String status;

    @NotNull
    private Integer id;

    @NotEmpty
    private String courierId;

    private String reason;

    public String getStatus() {
        return status.toUpperCase();
    }
}
