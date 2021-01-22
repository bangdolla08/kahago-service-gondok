package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum  OutGoingCounterEnum {
    BAGGING(0,"Bagging","Bagging"),
    OUTGOING(1,"Outgoing proses","Outgoing Proses"),
    RECEIVE_IN_WAREHOUSE(2,"Receive In Warehouse","Diterima diwarehouse"),
    REJECT_WAREHOUSE(3,"Reject Warehouse","Dibatalkan diwarehouse");
    private int code;
    private String string;
    private String keterangan;
    OutGoingCounterEnum(int code,String string, String keterangan) {
        this.code = code;
        this.string=string;
        this.keterangan=keterangan;
    }
    public int getCode() {
        return code;
    }

    public String getCodeString(){
        return String.valueOf(getCode());
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    private final static Map<Integer, OutGoingCounterEnum> map =
            Arrays.stream(OutGoingCounterEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<OutGoingCounterEnum> getPaymentEnumList(){
        List<OutGoingCounterEnum> paymentEnums=new ArrayList<>();
        paymentEnums.add(BAGGING);
        paymentEnums.add(OUTGOING);
        paymentEnums.add(RECEIVE_IN_WAREHOUSE);
        paymentEnums.add(REJECT_WAREHOUSE);
        return paymentEnums;
    }

    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static OutGoingCounterEnum getPaymentEnum(int code){
        return map.get(code);
    }
    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static OutGoingCounterEnum getPaymentEnum(String code){
        return map.get(Integer.parseInt(code));
    }

    /**
     * Get enum Integer
     * @return nilai Integer
     */
    public int getValue(){
        return code;
    }

    /**
     * Get Description String
     * @return Hasil yang lebih bisa di baca manusia
     */
    @Override
    public String toString() {
        return string;
    }
}
