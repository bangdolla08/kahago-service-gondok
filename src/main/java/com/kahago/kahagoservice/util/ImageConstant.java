package com.kahago.kahagoservice.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Hendro yuwono
 */
@Component
public class ImageConstant {
    public static final String PREFIX_PATH_IMAGE_TUTORIAL = "images/tutorial/";
    public static final String PREFIX_PATH_IMAGE_BANK = "images/bank/";
    public static final String PREFIX_PATH_IMAGE_COUPON = "images/coupon/";
    public static final String PREFIX_PATH_IMAGE_PAYMENT_OPTION="images/optionpayment/";
    public static final String PREFIX_PATH_IMAGE_VENDOR="images/vendor/";
    public static final String PREFIX_PATH_IMAGE_PICKUP="images/pickup/";

    private static String host;

    @Value("${kahago.host}")
    public void setHost(String host) {
        ImageConstant.host = host;
    }

    public static String reversePathPickupToUrl(String localPath) {
        if (Strings.isEmpty(localPath)) {
            return "";
        }
        return host + PREFIX_PATH_IMAGE_PICKUP + localPath.substring(localPath.lastIndexOf("/") + 1);
    }

    public static String reversePathVendorToUrl(String localPath) {
        if (Strings.isEmpty(localPath)) {
            return "";
        }
        return host + PREFIX_PATH_IMAGE_VENDOR + localPath.substring(localPath.lastIndexOf("/") + 1);
    }


}
