package com.kahago.kahagoservice.enummodel;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Hendro yuwono
 */
public enum DepositTypeEnum {
    DEPOSIT("Deposit", "0"),
    CREDIT("Credit", "1");

    private String type;
    private String value;

    DepositTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    private final static Map<String, DepositTypeEnum> map =
            Arrays.stream(DepositTypeEnum.values()).collect(toMap(leg -> leg.type, leg -> leg));


    /**
     * Untuk Mendapatkan Deposit enum
     * @param code Int Enum
     * @return Enmum
     */
    public static DepositTypeEnum getDepositTypeEnum(String code){
        return map.get(code);
    }
    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }
}
