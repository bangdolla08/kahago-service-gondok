package com.kahago.kahagoservice.enummodel;
import static java.util.stream.Collectors.toMap;
import java.util.Arrays;
import java.util.Map;

public enum DiscountEnum {
    SUCCESS("00","Success","Berhasil"),
    FAILED("99","Failed","Gagal"),
    NOT_FOUND("14","Not Found","Data Tidak ditemukan"),
    NOT_ACTIVE("02","Not Active","Kupon Sudah Tidak Aktif"),
    MIN_NOMINAL_TRX("18","Amount insufficient","Nominal tidak mencukupi jumlah minimal transaksi"),
    EXPIRED_VOUCHER("GX","Expired Voucher","Voucher sudah kadaluarsa"),
    NOT_YET_VOUCHER("GL","Not Used Voucher Yet","Voucher Belum Bisa Digunakan"),
	NOT_USED("I9","Coupon is Not Used","Kupon Tidak Dapat Digunakan");
    private String code;
    private String string;
    private String keterangan;

    private DiscountEnum(String code,String string, String keterangan) {
        this.code = code;
        this.string=string;
        this.keterangan=keterangan;
    }

    public String getCode() {
		return code;
	}

	public void setCode(String code) {
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

	private final static Map<String, DiscountEnum> map =
            Arrays.stream(DiscountEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));


    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static DiscountEnum getPaymentEnum(int code){
        return map.get(code);
    }

    /**
     * Get enum Integer
     * @return nilai Integer
     */
    public String getValue(){
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
