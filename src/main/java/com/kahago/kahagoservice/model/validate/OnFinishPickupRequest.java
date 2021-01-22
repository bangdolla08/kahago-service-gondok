package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickCourierOnFinishMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@PickCourierOnFinishMustMatch
public class OnFinishPickupRequest implements PickCourierOnFinishMustMatch.PickupStatusOnFinish {

    private String courierId;

    @NotEmpty
    private String status;

}
