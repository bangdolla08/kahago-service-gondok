package com.kahago.kahagoservice.model.dto;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.model.response.ProfileRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private MUserEntity mUserEntity;
    private ProfileRes profileRes;
    private BigDecimal balance;
}
