package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum FirebaseEnum {
    DEPOSIT(1,"Topup","Topup Deposit"),
    BOOKING(2,"Booking","Booking"),
    REFUND(3,"Refund","Refund");
    private int code;
    private String string;
    private String keterangan;

    private FirebaseEnum(int code,String string, String keterangan) {
        this.code = code;
        this.string=string;
        this.keterangan=keterangan;
    }

    public int getCode() {
		return code;
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

	private final static Map<Integer, FirebaseEnum> map =
            Arrays.stream(FirebaseEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<FirebaseEnum> getAllMutasiEnum() {
        List<FirebaseEnum> listOfMutasi = new ArrayList<>();
        listOfMutasi.add(DEPOSIT);
        listOfMutasi.add(BOOKING);
        listOfMutasi.add(REFUND);

        return listOfMutasi;
    }

    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static FirebaseEnum getPaymentEnum(int code){
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
        return string;
    }
}
