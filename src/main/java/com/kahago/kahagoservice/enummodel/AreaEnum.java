package com.kahago.kahagoservice.enummodel;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 24 Jun 2020
 */
public enum AreaEnum {
    PROV(1,"PROVINCE"),
    CITY(2,"KOTA"),
    KEC(3,"KECAMATAN"),
    KEL(4,"KELURAHAN"),
    VENDOR_AREA(5,"VENDOR AREA");
    private int number;
    private String stringValue;
    private AreaEnum(int number,String stringValue){
        this.number=number;
        this.stringValue=stringValue;
    }

    public int getNumber() {
        return number;
    }

    private final static Map<Integer, AreaEnum> map = 
    		Arrays.stream(AreaEnum.values()).collect(toMap(leg -> leg.number, leg -> leg));
    public static AreaEnum getEnum(Integer value) {
    	return map.get(value);
    }
    @Override
    public String toString() {
        return this.stringValue;
    }
}
