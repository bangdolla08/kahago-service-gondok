package com.kahago.kahagoservice.model.request;

import com.kahago.kahagoservice.validation.EmailIsExist;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author Hendro yuwono
 */
@Data
public class SignUpReq {
    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    @EmailIsExist
    private String email;

    @NotBlank
    private String noHp;
    private String referenceNumber;
}
