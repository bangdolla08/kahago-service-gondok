package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author bangd ON 25/02/2020
 * @project com.kahago.kahagoservice.model.response
 */
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourierManifestMonitoringRes {
    private Integer totalBook;
    private Integer totalItem;
    private Integer totalBookInCourier;
    private Integer totalItemInCourier;
    private ProfileRes.Profile profile;
    private Integer countManifest;
    private List<ManifestBook> detail;
    @Builder
    @Data
    public static class ManifestBook{
        private BookDataResponse bookDataResponse;
        private String pickupDestination;
        private String manifestNumber;
    }

}
