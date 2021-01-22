package com.kahago.kahagoservice.enummodel;
import static java.util.stream.Collectors.toMap;
import java.util.Arrays;
import java.util.Map;

public enum PayLaterEnum {
    PENDING(0,"Pending","Belum Terbayar"),
    REQUEST(1,"Paid","Terbayar"),
    DEPOSIT_PAY(2,"Verification Paying","Proses Verifikasi Pembayaran");
    private int code;
    private String string;
    private String keterangan;

    private PayLaterEnum(int code,String string, String keterangan) {
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

	private final static Map<Integer, PayLaterEnum> map =
            Arrays.stream(PayLaterEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));


    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static PayLaterEnum getPaymentEnum(int code){
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
