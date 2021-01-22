package com.kahago.kahagoservice.enummodel;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;



/**
 * @author Ibnu Wasis
 */
public enum FilterBookingEnum {
	VIEW_ALL(0,"Tampilkan Semua"),
    SELF_SENDER(1, "Saya Sebagai Pengirim"),
    SELF_RECEIVER(2, "Saya Sebagai Penerima"),
    ID_USER(3, "ID Reference / User Id");
    private Integer code;
    private String view;
    FilterBookingEnum(Integer code,String view){
        this.code=code;
        this.view=view;
    }
    private final static Map<Integer, FilterBookingEnum> map =
            Arrays.stream(FilterBookingEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static FilterBookingEnum getFilterBookingEnum(Integer code){
        if(code==null)
            code=0;
        return map.get(code);
    }

    public Integer getCode(){
        return this.code;
    }

    @Override
    public String toString() {
        return this.view;
    }
}
