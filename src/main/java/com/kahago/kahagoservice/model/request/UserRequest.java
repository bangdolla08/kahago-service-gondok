package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
public class UserRequest {
    private Integer idUserCategory;
    private String depositType;
    private Integer courierFlag;
    private String creditDay;
    private String refNum;
}
