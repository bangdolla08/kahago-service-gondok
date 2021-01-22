package com.kahago.kahagoservice.enummodel;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public enum PawoonStatusEnum {
    PENDING(1,"Pending"),
    SUCCESS(2,"Success"),
	UNKNOWN(9,"Failed");
    private int number;
    private String stringValue;
    private PawoonStatusEnum(int number,String stringValue){
        this.number=number;
        this.stringValue=stringValue;
    }

    public int getNumber() {
        return number;
    }

    private final static Map<Integer, PawoonStatusEnum> map =
            Arrays.stream(PawoonStatusEnum.values())
            .collect(toMap(leg -> leg.number, leg -> leg));
    public static PawoonStatusEnum getEnum(Integer code){
        return map.get(code);
    }
    @Override
    public String toString() {
        return this.stringValue;
    }
}
