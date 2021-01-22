package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kahago.kahagoservice.security.ResponseAuth;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileRes {
    private Integer creditDay;
    private String depositType;
    private Boolean flagUser;
    private Boolean flagMitra;
    private Integer accountType;
    private double feeDepositPertama;
    private double feeDepositLanjutan;
    private double feeTrx;
    private long referenceUser;
    private Integer userCategoryId;
    private String userCategoryName;
    private BigDecimal balance;
    private Profile profile;
    private ResponseAuth authentication;
    private List<Branch> branch;
    @JsonProperty(value="request_1")
    private Boolean request1;
    @JsonProperty(value="request_2")
    private Boolean request2;
    @JsonProperty(value="request_3")
    private Boolean request3;
    private Integer minKiriman;
    private Boolean paylater;
    private Integer userLevel;

    @Data
    @Builder
    public static class Profile {
        private String name;
        private String userId;
        private String email;
        private String sex;
        private String hp;
        private String address;
        private String referenceNumber;
        private String postalCode;
        private String kelurahan;
        private String kecamatan;
        private String kota;
        private String provinsi;
        private String placeOfBirth;
        private String dateOfBirth;
        private String idType;
        private String idNo;
        private String bankCode;
        private String noRekening;
        private String namaRekening;
    }

    @Data
    @Builder
    public static class Branch {
        private String officeName;
        private String officeCode;
        private String unitType;
    }

}
