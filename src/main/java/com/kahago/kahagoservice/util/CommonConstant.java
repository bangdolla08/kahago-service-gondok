package com.kahago.kahagoservice.util;

import java.security.SecureRandom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Hendro yuwono
 */
public class CommonConstant {
    public static String CONFIRM_DEPOSIT = "Konfirmasi Deposit Atas Nomor Tiket #tiket |"
            + "Telah Diterima dan Sedang Diproses |"
            + "Silahkan menunggu atau konfirmasi ke Customer Service";

    public static String dateFormatter(String pattern, LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    public static double greaterThan(double x, double y, double z) {
        double max;

        if (x >= y) {
            max = x;
        } else {
            max = y;
        }

        return max >= z ? max : z;
    }
    public static boolean toBoolean(int value) {
        return value == 1;
    }

    public static String randomString(int length) {
        String character = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(character.charAt(new SecureRandom().nextInt(character.length())));
        return sb.toString();
    }
}
