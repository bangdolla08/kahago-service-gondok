package com.kahago.kahagoservice.enummodel;

import lombok.Getter;

public enum SaveTrxEnum {
    UNSAVE("0"),
    SAVE("1");
	
    public String getFlag() {
		return flag;
	}
	String flag;
    SaveTrxEnum(String flag){
    	this.flag = flag;
    }
}
