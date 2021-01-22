package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 28 Nov 2019
 */
public enum OfficeTypeEnum {
    PUSAT("0","Office","Kantor"),
    WAREHOUSE("1","Warehouse","Gudang"),
    COUNTER("2","Counter","Loket");
    private String code;
    private String string;
    private String keterangan;

    OfficeTypeEnum(String code,String string, String keterangan) {
        this.code = code;
        this.string=string;
        this.keterangan=keterangan;
    }

    public String getCode() {
		return code;
	}

	public String getCodeString(){
        return String.valueOf(getCode());
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

	private final static Map<String, OfficeTypeEnum> map =
            Arrays.stream(OfficeTypeEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<OfficeTypeEnum> getPaymentEnumList(){
        List<OfficeTypeEnum> paymentEnums=new ArrayList<>();
        paymentEnums.add(PUSAT);
        paymentEnums.add(WAREHOUSE);
        paymentEnums.add(COUNTER);
        return paymentEnums;
    }

    /**
     * Untuk Mendapatkan Payment enum
     * @param code String Enum
     * @return Enmum
     */
    public static OfficeTypeEnum getPaymentEnum(String code){
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
