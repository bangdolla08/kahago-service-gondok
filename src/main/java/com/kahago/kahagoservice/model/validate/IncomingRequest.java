package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.validation.IncomingMustMatch;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@IncomingMustMatch
public class IncomingRequest implements IncomingMustMatch.Incoming {

    @NotEmpty
    @NotNull
    private String officeCode;

    @NotEmpty
    @NotNull
    private String qrCode;
}
