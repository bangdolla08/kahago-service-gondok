package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum PickupEnum {
    ASSIGN_PICKUP(0, "Pickup Ongoing", "ASSIGN_PICKUP"),
    IN_COURIER(1, "In Courier", "IN_COURIER"),
    ACCEPT_IN_WAREHOUSE(2, "Accept In Warehouse", "ACCEPT_IN_WAREHOUSE"),
    HOLD_BY_WAREHOUSE(3, "Hold By Warehouse", "HOLD_BY_WAREHOUSE"),
    DRAFT(4, "Draft", "DRAFT"),
    ISSUES_IN_ADDRESS(5, "Issue In Address", "ISSUES_IN_ADDRESS"),
    HISTORY(9, "History", "HISTORY");

    private static final Map<Integer, PickupEnum> map =
            Arrays.stream(PickupEnum.values()).collect(toMap(leg -> leg.value, leg -> leg));

    private Integer value;
    private String string;
    private String key;

    PickupEnum(int value, String s, String key) {
        this.value = value;
        this.string = s;
        this.key = key;
    }

    public static PickupEnum getEnumByNumber(Integer i) {
        return map.get(i);
    }

    public String getString() {
        return string;
    }

    public int getValue() {
        return this.value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
