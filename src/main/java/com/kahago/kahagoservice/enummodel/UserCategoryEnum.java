package com.kahago.kahagoservice.enummodel;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 27 Nov 2019
 */
public enum UserCategoryEnum {
    COUNTER(0,"Counter"),
    CUSTOMER(1,"Customer"),
    MARKETING(2,"Marketing"),
    MITRA(3,"Mitra"),
    SUPERVISOR_MARKETING(4,"Supervisor Marketing"),
    TIKI_REQ(5,"TIKI Request"),
    TIKI_BOOK(6,"TIKI Booking"),
    EXTRA_MITRA(7,"TIKI Booking"),
    EXTRA_CUSTOMER(8,"TIKI Booking"),
    ADMIN_INPUT(9,"TIKI Booking"),
    SALES_KAHA(10,"Sales Kaha"),
    BANNED(11,"Banned"),
    WAREHOUSE(12,"Warehouse"),
    OPERATIONAL(13,"Operational"),
    ADMIN(14,"Admin"),
    KEUANGAN(15,"Keuangan")
    ;

    private String type;
    private Integer value;

    UserCategoryEnum(Integer value, String type) {
        this.type = type;
        this.value = value;
    }

    private final static Map<Integer, UserCategoryEnum> map =
            Arrays.stream(UserCategoryEnum.values()).collect(toMap(leg -> leg.value, leg -> leg));
    public static UserCategoryEnum getEnum(int code){
        return map.get(code);
    }
    public Integer getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }
}
