package com.kahago.kahagoservice.security;

/**
 * @author Hendro yuwono
 */
public class SecurityConstant {
    public static final String PREFIX_PATH_IMAGE = "/images/switcherEntity/";
    public static final String SECRET_KEY = "101S3cURiTy101";
    public static final long EXPIRED_TOKEN = 360;
    public static final String PREFIX_HEADER_AUTH = "Bearer ";
    public static final String HEADER_AUTH = "Authorization";

    public static String rolesUser(int level) {
        if (level == 1) {
            return "ROLE_USER";
        } else if (level == 2) {
            return "ROLE_ADMIN";
        } else if (level == 7) {
            return "ROLE_BOC";
        } else {
            return "ROLE_SUPER";
        }
    }
}
