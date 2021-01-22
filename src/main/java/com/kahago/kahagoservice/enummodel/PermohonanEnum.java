package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 10 Jun 2020
 */
public enum PermohonanEnum {
    DRAFT(0,"Request Permohonan"),
    PROPOSED(1,"Print"),
    WAITING_APPROVE(2,"Waiting Approve"),
    APPROVE(3, "Approve"),
    DONE(4,"Done");
    private int value;
    private String string;
    PermohonanEnum(int value,String s){
        this.value=value;
        this.string=s;
    }
    private final static Map<Integer, PermohonanEnum> map =
            Arrays.stream(PermohonanEnum.values()).collect(toMap(leg -> leg.value, leg -> leg));

    public int getValue(){
        return this.value;
    }

    public static PermohonanEnum getEnumByNumber(Integer i){
        return map.get(i);
    }

    @Override
    public String toString() {
        return this.string;
    }
}
