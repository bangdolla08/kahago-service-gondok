package com.kahago.kahagoservice.enummodel;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

/**
 * @author bangd ON 01/12/2019
 * @project com.kahago.kahagoservice.enummodel
 */
public enum  DepositEnum {
//    -1= Request Pay, 0 = Request,1 = Konfirmasi,2 = Sudah di Topup/Proses,3 = Ditolak
	REQ_PAY("Request Paylater",-1),
    REQUEST("Request",0),
    KONFIRMASI("Konfirmasi",1),
    PROSES_TOPUP("Sudah di Topup/Proses",2),
    CANCEL("Ditolak",3);
    DepositEnum(String string,Integer value){
        this.string=string;
        this.value=value;
    }
    private Integer value;
    private String string;

    @Override
    public String toString() {
        return string;
    }

    public Integer getValue() {
        return value;
    }
    
    private final static Map<Integer, DepositEnum> map = 
    		Arrays.stream(DepositEnum.values()).collect(toMap(leg -> leg.value, leg -> leg));
    
    public static String getKeterangan(Integer value) {
    	return map.get(value).toString();
    }
}
