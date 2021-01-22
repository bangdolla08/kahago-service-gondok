package com.kahago.kahagoservice.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Hendro yuwono
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class PickupResponse {
    private Integer id;
    private String status;
    private String dateOfAssign;
    private String manifest;
    private String name;
    private String phone;
    private String address;
    private String description;
    private Geometry location;

    @Data
    @SuperBuilder
    public static class Geometry {
        private String type;
        private Double[] coordinates;
    }
}
