package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum WarehouseEnum {
    RECEIVE_IN_WAREHOUSE("Receive In Warehouse",0, "Barang Diterima Warehouse"),
    APPROVE("Approval",1,"Aproved"),
    HOLD_WAREHOUSE("Hold By Warehouse",2,"Barang Bermasalah"),
    CANCEL_WAREHOUSE("Cancel Warehouse",3,"Batal Booking"),
    READY_TO_OUTGOING("Ready To Outgoing",4,"Bagging"),
    OUTGOING("Outgoing",5,"Outgoing");

    private Integer code;
    private String string;
    private String keterangan;
    private String codeString;

    private final static Map<Integer, WarehouseEnum> map =
            Arrays.stream(WarehouseEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    /**
     * Untuk mendapatkan Wharehouse dengan menggunakan status 1,2,3,4,5
     * @param code bentuk Int
     * @return Enum yang sudah Di cari
     */
    public static WarehouseEnum getWarehouseEnum(int code){
        return map.get(code);
    }

    /**
     * Untuk mendapatkan Wharehouse dengan menggunakan status 1,2,3,4,5
     * @param code bentuk String
     * @return Enum yang sudah Di cari
     */
    public static WarehouseEnum getWarehouseEnum(String code){
        return map.get(Integer.parseInt(code));
    }

    public static List<WarehouseEnum> getListWarehouse(){
        List<WarehouseEnum> warehouseEnums=new ArrayList<>();
        warehouseEnums.add(RECEIVE_IN_WAREHOUSE);
        warehouseEnums.add(APPROVE);
        warehouseEnums.add(HOLD_WAREHOUSE);
        warehouseEnums.add(CANCEL_WAREHOUSE);
        warehouseEnums.add(READY_TO_OUTGOING);
        warehouseEnums.add(OUTGOING);
        return warehouseEnums;
    }

    private WarehouseEnum(String toString, int value, String showData){
        this.string=toString;
        this.code=value;
        this.keterangan=showData;
        this.codeString=value+"";

    }

    @Override
    public String toString() {
        return this.keterangan;
    }

    public int getValue(){
        return this.code;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getValueString(){
        return this.codeString;
    }

    public String getKeterangan(){
        return this.keterangan;
    }

    public String getString(){
        return this.string;
    }
}
