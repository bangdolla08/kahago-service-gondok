package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.PickTitipanBookingMustMatch;
import com.kahago.kahagoservice.validation.PickTitipanNonBookingMustMatch;
import com.kahago.kahagoservice.validation.RequestPickupMustExist;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@PickTitipanNonBookingMustMatch
@RequestPickupMustExist
public class OnBookingTitipanRequest implements PickTitipanBookingMustMatch.OnAccept {
    @NotNull
    private Integer id;

    @NotNull
    private String bookId;

    @NotEmpty
    private String courierId;

    @NotEmpty
    private String qrCode;

    @NotNull
    private MultipartFile image;

    @NotNull
    private Integer partId;
}
