package com.kahago.kahagoservice.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author Hendro yuwono
 */
@Data
public class EditProfileReq {
    private String name;
    private String email;
    private String address;
    @NotEmpty
    private Integer idPostalCode;
    private String hp;
    private String placeOfBirth;
    private String dateOfBirth;
    private String idType;
    private String idNo;
    private String bankCode;
    private String namaRekening;
    private String noRekening;
}
