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
public enum DeviceEnum {
    ANDROID(0,"Android","Android"),
    WEB(1,"Browser","Browser"),
    IOS(2,"IOS","IOS");
    private int code;
    private String string;
    private String keterangan;

    DeviceEnum(int code,String string, String keterangan) {
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

	private final static Map<Integer, DeviceEnum> map =
            Arrays.stream(DeviceEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<DeviceEnum> getPaymentEnumList(){
        List<DeviceEnum> paymentEnums=new ArrayList<>();
        paymentEnums.add(ANDROID);
        paymentEnums.add(WEB);
        paymentEnums.add(IOS);
        return paymentEnums;
    }

    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static DeviceEnum getPaymentEnum(int code){
        return map.get(code);
    }
    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static DeviceEnum getPaymentEnum(String code){
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
