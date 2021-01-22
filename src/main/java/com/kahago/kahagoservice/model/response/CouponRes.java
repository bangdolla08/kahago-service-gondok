package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hendro yuwono
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRes {
    private String image;
    private String title;
    private String code;
    private String expiredDate;
    private String description;
}
