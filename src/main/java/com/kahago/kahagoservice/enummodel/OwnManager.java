package com.kahago.kahagoservice.enummodel;

/**
 * @author Hendro yuwono
 */
public class OwnManager {

    public static final String PREFIX_PATH_IMAGE = "/images/switcherEntity/";
    public static final String SECRET_KEY = "K03nTh0l";
    public static final long EXPIRED_TOKEN = 1 * 24 * 60 * 60 * 1000;
    public static final String PREFIX_HEADER_AUTH = "Bearer ";
    public static final String HEADER_AUTH = "Authorization";

    public static String serviceType(String serviceType) {
        if (serviceType.equals("0")) {
            return "Package";
        } else if (serviceType.equals("1")) {
            return "Document";
        } else {
            return "Package & Document";
        }
    }

    public static boolean isStatusValue(String id) {
        return !id.equals("0");
    }

    public static String rolesUser(int level) {
        if (level == 1) {
            return "ROLE_USER";
        } else if (level == 2) {
            return "ROLE_ADMIN";
        } else {
            return "ROLE_SUPER";
        }
    }
}
