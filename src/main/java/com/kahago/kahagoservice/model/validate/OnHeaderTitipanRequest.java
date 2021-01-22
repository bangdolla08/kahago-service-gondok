package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickTitipanNonBookingMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@PickTitipanNonBookingMustMatch
public class OnHeaderTitipanRequest implements PickTitipanNonBookingMustMatch.OnAccept {

    @NotNull
    private Integer id;

    @NotEmpty
    private String courierId;
}
