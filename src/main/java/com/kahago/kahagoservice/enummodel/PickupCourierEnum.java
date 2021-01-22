package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hendro yuwono
 */
public enum PickupCourierEnum {

    DRAFT(0, "DRAFT"),
    READY_PICKUP(1, "READY_PICKUP"),
    OTW_CUSTOMER(2, "OTW_CUSTOMER"),
    PROCESS_PICKUP(3, "PROCESS_PICKUP"),
    ISSUES_IN_ADDRESS(4, "ISSUES_IN_ADDRESS"),
    OTW_WAREHOUSE(5, "OTW_WAREHOUSE"),
    FINISH_PICKUP(6, "FINISH_PICKUP"),
    FINISH(7, "FINISH");

    private Integer value;
    private String key;

    PickupCourierEnum(Integer value, String key) {
        this.value = value;
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    static List<PickupCourierEnum> all = Stream.of(PickupCourierEnum.values()).collect(Collectors.toList());

    public static List<Integer> showInReadyPickup() {
        List<Integer> values = Arrays.asList(1, 4);
        return all.stream().map(PickupCourierEnum::getValue).filter(values::contains).collect(Collectors.toList());
    }

    public static List<Integer> showInFinishPickup() {
        List<Integer> values = Collections.singletonList(6);
        return all.stream().map(PickupCourierEnum::getValue).filter(values::contains).collect(Collectors.toList());
    }

    public static List<Integer> showInProcessPickup() {
        List<Integer> values = Arrays.asList(2, 3);
        return all.stream().map(PickupCourierEnum::getValue).filter(values::contains).collect(Collectors.toList());
    }

    public static PickupCourierEnum getByValue(Integer value){
        return all.stream().filter(v -> v.value.equals(value)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public static PickupCourierEnum getByKey(String key) {
        return all.stream().filter(v -> v.key.equals(key)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public static List<PickupCourierEnum> all() {
        return all;
    }

}
