package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum VendorServiceTypeEnum {
    PACKAGE(0,"Package"),
    DOCUMENT(1,"Document"),
    PACKAGE_DOCUMENT(2,"Package & Dokumen");
    Integer typeService;
    String description;
    VendorServiceTypeEnum(Integer typeService, String description){
        this.typeService=typeService;
        this.description=description;
    }

    public String getDescription(){
        return description;
    }
    public Integer getTypeService(){
        return typeService;
    }

    private final static Map<Integer, VendorServiceTypeEnum> map =
            Arrays.stream(VendorServiceTypeEnum.values()).collect(toMap(leg -> leg.typeService, leg -> leg));
    public static VendorServiceTypeEnum getPaymentEnum(Integer code){
        return map.get(code);
    }
}
