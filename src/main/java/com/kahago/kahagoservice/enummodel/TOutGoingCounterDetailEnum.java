package com.kahago.kahagoservice.enummodel;
/**
 * @author Ibnu Wasis
 */
public enum TOutGoingCounterDetailEnum {
	PENDING(0,"Pending","Belom Proses"),
    PROCESS(1,"Process","Proses"),
    RECEIVE(2,"Receive","Terima Barang By Warehouse"),
    REJECT(3,"Reject","Reject By Warehouse");
    private int code;
    private String string;
    private String keterangan;
    private TOutGoingCounterDetailEnum(int code,String string, String keterangan) {
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
}
