package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 17/11/2019
 * @project com.kahago.kahagoservice.model.response
 */

@Data
@Builder
public class KecamatanResponse {
    private Integer idKecamatan;
    private String kecamatan;
    private String kota;
    private String province;
}
