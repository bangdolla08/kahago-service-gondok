package com.kahago.kahagoservice.enummodel;

/**
 * @author bangd ON 22/11/2019
 * @project com.kahago.kahagoservice.enummodel
 */
public enum  OptionPaymentEnum {
    PAYMENT(1,"Payment"),
    TOP_UP(2,"Top Up");
    private Integer typePayment;
    private String title;
    OptionPaymentEnum(Integer typePayment,String title){
        this.typePayment=typePayment;
        this.title=title;
    }

    public Integer getTypePayment() {
        return typePayment;
    }

    public String getTitle() {
        return title;
    }
}
