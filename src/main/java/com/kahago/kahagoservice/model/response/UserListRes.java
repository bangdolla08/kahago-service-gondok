package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 18/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@JsonSerialize
@Builder
@JsonInclude(value=Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserListRes {
    private String userId;
    private String userName;
    private String userPhoneNumber;
    private String userCategory;
    private Integer userCategoryId;
}
