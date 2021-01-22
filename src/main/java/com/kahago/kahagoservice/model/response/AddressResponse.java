package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 20/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {
    private Integer id;
    private String name;
    private String address;
    private String telp;
    private String userId;
    private String email;
    private Integer postalId;
    private String kecamatan;
    private String kelurahan;
    private String kota;
    private String provinsi;
    private String postalCode;
    private int areaId;
}
