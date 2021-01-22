package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class OriginsV1Res {

    private String rc;
    private String description;
    private List<Origin> origins;

    @Data
    @Builder
    public static class Origin {
        private String status;
        private String areaOriginId;
        private String areaOriginName;
    }
}
