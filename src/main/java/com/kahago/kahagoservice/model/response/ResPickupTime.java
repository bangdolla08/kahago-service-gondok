package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class ResPickupTime {
    private Integer pickupTimeId;
    private String pickupDate;
    private String pickupTime;
    private String pickupDay;
    private Boolean isActived;
    private Integer idUserCategory;
}
