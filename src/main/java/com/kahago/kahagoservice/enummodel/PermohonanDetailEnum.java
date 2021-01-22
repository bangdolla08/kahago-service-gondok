package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 10 Jun 2020
 */
public enum PermohonanDetailEnum {
	DELETE(-1,"Delete permohonan"),
    DRAFT(0,"Request Permohonan"),
    VERIFIED(1,"Print"),
    WAITING_APPROVE(2,"Waiting Approve"),
    REJECT(3,"Print");
    private int value;
    private String string;
    PermohonanDetailEnum(int value,String s){
        this.value=value;
        this.string=s;
    }
    private final static Map<Integer, PermohonanDetailEnum> map =
            Arrays.stream(PermohonanDetailEnum.values()).collect(toMap(leg -> leg.value, leg -> leg));

    public int getValue(){
        return this.value;
    }

    public static PermohonanDetailEnum getEnumByNumber(Integer i){
        return map.get(i);
    }

    @Override
    public String toString() {
        return this.string;
    }
}
