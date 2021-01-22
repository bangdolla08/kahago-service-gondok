package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class UserOfCategoryResp {
    private Integer userCateroryId;
    private String userCategoryName;
    private String userCategoryRole;
    private String depositType;
    private Integer courierFlag;
    private String creditDay;
    private String referenceNumber;
    private String name;
    private String userId;
    private String email;
    private String sex;
    private String hp;
}
