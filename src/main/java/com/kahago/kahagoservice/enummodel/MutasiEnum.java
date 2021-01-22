package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum MutasiEnum {
    DEPOSIT(1,"Deposit","Topup Deposit"),
    BOOKING(2,"Booking","Booking"),
    REFUND(3,"Refund","Refund"),
    EDIT_BOOK_ADD(4,"Edit Book Add","Edit Book Add"),
    EDIT_BOOK_REFUND(5,"Edit Book Refund","Edit Book Refund");
    private int code;
    private String string;
    private String keterangan;

    MutasiEnum(int code,String string, String keterangan) {
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

	private final static Map<Integer, MutasiEnum> map =
            Arrays.stream(MutasiEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<MutasiEnum> getAllMutasiEnum() {
        List<MutasiEnum> listOfMutasi = new ArrayList<>();
        listOfMutasi.add(DEPOSIT);
        listOfMutasi.add(BOOKING);
        listOfMutasi.add(REFUND);
        listOfMutasi.add(EDIT_BOOK_ADD);
        listOfMutasi.add(EDIT_BOOK_REFUND);

        return listOfMutasi;
    }

    /**
     * Untuk Mendapatkan Mutasi enum
     * @param code Int Enum
     * @return Enmum
     */
    public static MutasiEnum getMutasiEnum(Integer code){
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
