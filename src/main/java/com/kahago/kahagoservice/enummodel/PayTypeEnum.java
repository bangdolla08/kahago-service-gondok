package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum PayTypeEnum {
    PAY_LATER(0,"Deposit"),
    PAY_NOW(1,"Booking");
    private int code;
    private String keterangan;

    PayTypeEnum(int code, String keterangan) {
        this.code = code;
        this.keterangan=keterangan;
    }

    public int getCode() {
		return code;
	}
    public String getCodeString() {
		return String.valueOf(code);
	}

	public void setCode(int code) {
		this.code = code;
	}


	public String getKeterangan() {
		return keterangan;
	}

	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}

	private final static Map<Integer, PayTypeEnum> map =
            Arrays.stream(PayTypeEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    /**
     * Untuk Mendapatkan Mutasi enum
     * @param code Int Enum
     * @return Enmum
     */
    public static PayTypeEnum getEnum(Integer code){
        return map.get(code);
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
        return keterangan;
    }
}
