package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.enummodel
 */
public enum RequestPickupEnum {
    REQUEST(0, "Request", "REQUEST"),
    ASSIGN_PICKUP(1, "Assign Pickup", "ASSIGN_PICKUP"),
    IN_COURIER(2, "In Courier", "IN_COURIER"),
    IN_WAREHOUSE(3, "In Warehouse", "IN_WAREHOUSE"),
    FINISH_BOOK(4, "Finish Book", "FINISH_BOOK"),
    DRAFT_PICKUP(5, "DRAFT PICKUP", "DRAFT_PICKUP"),
    CANCEL_DETAIL(6, "Cancel Detail", "CANCEL_DETAIL"),
    REJECT_BY_COURIER(7, "Reject By Courier", "REJECT_BY_COURIER"),
    EXPIRED_PAYMENT(17, "Expired Payment", "EXPIRED_PAYMENT");

    private final static Map<Integer, RequestPickupEnum> map =
            Arrays.stream(RequestPickupEnum.values()).collect(toMap(leg -> leg.value, leg -> leg));
    private Integer value;
    private String string;
    private String key;

    RequestPickupEnum(Integer value, String string, String key) {
        this.value = value;
        this.string = string;
        this.key = key;
    }

    static List<RequestPickupEnum> all = Stream.of(RequestPickupEnum.values()).collect(Collectors.toList());


    public static RequestPickupEnum getByValue(Integer value){
        return all.stream().filter(v -> v.value.equals(value)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public static Integer toPaymentEnumInteger(Integer status) {
        PaymentEnum anEnum = PaymentEnum.getPaymentEnum(status);
        switch (anEnum) {
            case REQUEST:
                return RequestPickupEnum.REQUEST.getValue();
            case ASSIGN_PICKUP:
                return RequestPickupEnum.ASSIGN_PICKUP.getValue();
            case PICKUP_BY_KURIR:
                return RequestPickupEnum.IN_COURIER.getValue();
            case RECEIVE_IN_WAREHOUSE:
                return RequestPickupEnum.IN_WAREHOUSE.getValue();
            case DRAFT_PICKUP:
                return RequestPickupEnum.DRAFT_PICKUP.getValue();
            case FINISH_BOOK:
                return RequestPickupEnum.FINISH_BOOK.getValue();
            default:
                return null;
        }
    }

    public static PaymentEnum toPaymentEnum(Integer status) {
        RequestPickupEnum requestPickupEnum = RequestPickupEnum.getPaymentEnum(status);
        switch (requestPickupEnum) {
            case REQUEST:
                return PaymentEnum.REQUEST;
            case ASSIGN_PICKUP:
                return PaymentEnum.ASSIGN_PICKUP;
            case IN_COURIER:
                return PaymentEnum.PICKUP_BY_KURIR;
            case IN_WAREHOUSE:
                return PaymentEnum.RECEIVE_IN_WAREHOUSE;
            case DRAFT_PICKUP:
                return PaymentEnum.DRAFT_PICKUP;
            case FINISH_BOOK:
                return PaymentEnum.FINISH_BOOK;
            default:
                return PaymentEnum.REQUEST;
        }
    }

    public static RequestPickupEnum getPaymentEnum(int code) {
        return map.get(code);
    }

    @Override
    public String toString() {
        return string;
    }

    public Integer getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
