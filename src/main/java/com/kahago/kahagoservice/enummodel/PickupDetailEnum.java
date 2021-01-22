package com.kahago.kahagoservice.enummodel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bangd ON 17/12/2019
 * @project com.kahago.kahagoservice.enummodel
 */

public enum PickupDetailEnum {
    IN_COURIER("Proses",1, "IN_COURIER"),
    IN_WAREHOUSE("Selesai",2, "IN_WAREHOUSE"),
    HOLD_WAREHOUSE("Hold by warehouse",3, "HOLD_WAREHOUSE"),
    REJECTED_PICKUP("Barang Bermasalah", 4, "REJECTED_PICKUP"),
    ASSIGN_PICKUP("On Going",0, "ASSIGN_PICKUP"),
    HISTORY("HISTORY",9, "HISTORY");

    // ASSIGN, REJECTED, IN_COUR

    private String string;
    private Integer value;
    private String key;

    PickupDetailEnum(String string, Integer value, String key){
        this.string = string;
        this.value = value;
        this.key = key;
    }

    static List<PickupDetailEnum> all = Stream.of(PickupDetailEnum.values()).collect(Collectors.toList());

    public static PickupDetailEnum byValue(Integer value) {
        return all.stream().filter(v -> v.value.equals(value)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public Integer getValue(){
        return value;
    }

    public String getString() {
        return string;
    }

    public String getKey() {
        return key;
    }
}
