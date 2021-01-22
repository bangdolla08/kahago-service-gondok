package com.kahago.kahagoservice.enummodel;

/**
 * @author Hendro yuwono
 */
public enum ResponseStatus {
    OK("00", "Berhasil"),
    INSUFICIENT_FUND("20","Insuficient Fund"),
    NOT_FOUND("14","Not Found"),
    FAILED("99","Failed"),
    NEED_APPROVAL("02","Need Approval"),
    NOT_USED("08","Not Used"),
    SESSION_TIMEOUT("GX","Session Timeout"),
    MIS_MATCH("10","Mis Match"),
    IN_PROCCESS("11","In Process"),
    LIMIT_TRANSFER("21","Transaction is limited"),
    NOT_PROCESS("12","Trx Can't be process, Please call Customer Service"),
    NOT_CHANGED("13","Trx Can't be Changed");

    private final String value;
    private final String reasonPhrase;

    ResponseStatus(String value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}