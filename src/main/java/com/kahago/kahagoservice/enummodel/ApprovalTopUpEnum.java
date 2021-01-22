package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum ApprovalTopUpEnum {
    REQUEST(0,"Request"),
    APPROVE(2,"Approval"),
    REJECT(3,"Ditolak");
    private int statusCode;
    private String statusCodeString;
    private String string;
    private ApprovalTopUpEnum(int statusCode,String title){
        this.statusCode=statusCode;
        this.statusCodeString=String.valueOf(statusCode);
        string=title;
    }
    private final static Map<Integer, ApprovalTopUpEnum> map =
            Arrays.stream(ApprovalTopUpEnum.values()).collect(toMap(leg -> leg.statusCode, leg -> leg));

    public static ApprovalTopUpEnum findApprovalId(String statusCodeString){
        return map.get(Integer.parseInt(statusCodeString));
    }

    public static ApprovalTopUpEnum findApprovalId(int statusCode){
        return map.get(statusCode);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusCodeString() {
        return statusCodeString;
    }
}
