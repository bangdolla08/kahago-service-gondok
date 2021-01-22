package com.kahago.kahagoservice.model.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author bangd ON 24/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
public class ManifestDetailRes {
    private LocalDate tglPickup;
    private String idCourier;
    private Integer status;
    private Integer idTimePickup;
    private String manifestCode;
    private Boolean isEditable;
    private ValidateTimeToAsiggn validateTimeToAsiggn;
    private Integer areaKotaId;
    private String kotaName;
    private List<BookDataResponse> detail;
    private List<AssignPickupResponse> pickupResponses;
}
