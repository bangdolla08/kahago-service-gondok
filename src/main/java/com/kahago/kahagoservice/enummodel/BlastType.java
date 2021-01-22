package com.kahago.kahagoservice.enummodel;

public enum BlastType {
    PROMO(1,"Promo"),
    COUPON(2,"Coupon"),
    TUTORIAL(3,"Tutorial");
    private Integer integer;
    private String string;

    BlastType(Integer integer,String string){
        this.integer=integer;
        this.string=string;
    }

    @Override
    public String toString() {
        return string;
    }
    public Integer getInteger(){
        return integer;
    }
}
