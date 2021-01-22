package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 17/11/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class KelurahanResponse {
    private Integer idPostalCode;
    private String postalCode;
    private String kelurahan;
    private Integer areaId;
    private String kecamatan;
    private Integer kotaId;
    private String kota;
    private Integer privasiId;
    private String provinsi;
}
