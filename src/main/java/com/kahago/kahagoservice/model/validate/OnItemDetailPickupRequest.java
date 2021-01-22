package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickCourierOnItemDetailMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@PickCourierOnItemDetailMustMatch
public class OnItemDetailPickupRequest implements PickCourierOnItemDetailMustMatch.ItemDetail {

    @NotNull
    private Integer id;

    @NotEmpty
    private String bookId;

    @NotEmpty
    private String courierId;
}
