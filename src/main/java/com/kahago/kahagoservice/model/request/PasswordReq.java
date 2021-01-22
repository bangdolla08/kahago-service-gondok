package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Hendro yuwono
 */

@Data
public class PasswordReq {
    private String oldPassword;
    private String newPassword;
    private String token;
    private String userId;
}
