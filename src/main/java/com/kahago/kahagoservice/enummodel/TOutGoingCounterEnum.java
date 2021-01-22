package com.kahago.kahagoservice.enummodel;
/**
 * @author Ibnu Wasis
 */
public enum TOutGoingCounterEnum {
	BAGGING(0,"Bagging Counter","Bagging Counter"),
    OUTGOING_COUNTER(1,"Outgoing Counter","Outgoing Counter"),
    RECEIVE_IN_WAREHOUSE(2,"Receive In Warehouse","Terima Barang By Warehouse");
	private int code;
    private String string;
    private String keterangan;
    private TOutGoingCounterEnum(int code,String string, String keterangan) {
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
    
    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
