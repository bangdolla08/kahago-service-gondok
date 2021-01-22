package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickTitipanNonBookingMustMatch;
import com.kahago.kahagoservice.validation.RequestPickupMustExist;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@PickTitipanNonBookingMustMatch
@RequestPickupMustExist
public class OnDeleteHeaderTitipanRequest implements PickTitipanNonBookingMustMatch.OnAccept, RequestPickupMustExist.OnAccept {
    @NotNull
    private Integer id;

    @NotEmpty
    private String courierId;

    @NotEmpty
    private String bookId;
}
