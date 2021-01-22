package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum  FlagStatusEnum {
    ACTIVE("1", "Aktif"),
    NON_ACTIVE("0", "Tidak Aktif");

    private String key;
    private String value;
    private Integer valueInteger;
    FlagStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
        this.valueInteger=Integer.parseInt(key);
    }

    private final static Map<String, FlagStatusEnum> map = Arrays.stream(FlagStatusEnum.values()).collect(toMap(leg -> leg.key, leg -> leg));

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public Integer getValueInteger() {
        return valueInteger;
    }
}
