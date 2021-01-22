package com.kahago.kahagoservice.enummodel;
/**
 * @author Ibnu Wasis
 */
public enum TutorialEnum {
	NULL_ENUM(0,"NULL"),
    TOUTORIAL(1,"Toutorial"),
    DASHBOARD_ANDROID(2,"Dashboard Android");
    private int number;
    private String stringValue;
    private TutorialEnum(int number,String stringValue){
        this.number=number;
        this.stringValue=stringValue;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return this.stringValue;
    }
}
