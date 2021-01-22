package com.kahago.kahagoservice.model.validate;

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
@PickCourierOnReadyMustMatch
public class OnReadyPickupRequest implements PickCourierOnReadyMustMatch.PickupStatusOnReady {
    @NotEmpty
    private String status;

    @NotNull
    private Integer id;

    @NotEmpty
    private String courierId;


    public String getStatus() {
        return status.toUpperCase();
    }
}
