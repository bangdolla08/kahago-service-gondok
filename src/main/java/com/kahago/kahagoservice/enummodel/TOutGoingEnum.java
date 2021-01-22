package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum  TOutGoingEnum {
    PENDING(0,"Pending","Belom Print"),
    PRINT(1,"Print","Print"),
    UPLOAD(2,"Upload","Upload Photo"),
    COURIER_DATA(3,"Courier Data Lengkap","Courier Data Lengkap");
    private int code;
    private String string;
    private String keterangan;
    TOutGoingEnum(int code,String string, String keterangan) {
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

    private final static Map<Integer, TOutGoingEnum> map =
            Arrays.stream(TOutGoingEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<TOutGoingEnum> getPaymentEnumList(){
        List<TOutGoingEnum> paymentEnums=new ArrayList<>();
        paymentEnums.add(PENDING);
        paymentEnums.add(PRINT);
        paymentEnums.add(UPLOAD);
        return paymentEnums;
    }

    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static TOutGoingEnum getPaymentEnum(int code){
        return map.get(code);
    }
    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static TOutGoingEnum getPaymentEnum(String code){
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
