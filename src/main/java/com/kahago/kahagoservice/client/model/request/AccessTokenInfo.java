package com.kahago.kahagoservice.client.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class AccessTokenInfo {
	private String accessToken;
    private String expiresIn;
    private String refreshToken;
    private String reExpiresIn;
    private String tokenStatus;
}
