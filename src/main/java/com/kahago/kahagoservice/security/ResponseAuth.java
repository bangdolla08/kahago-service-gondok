package com.kahago.kahagoservice.security;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class ResponseAuth {
    private String username;
    private String expired;
    private String accessToken;
}
