package com.kahago.kahagoservice.enummodel;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author bangd ON 31/12/2019
 * @project com.kahago.kahagoservice.enummodel
 */
public enum SaveSendStatusEnum {
    SAVE(1,"Save"),
    DRAFT(9,"Draft"),
    EDIT(2,"Edit"),
    DELETE(3,"Delete");
    private Integer saveNumber;
    private String status;
    SaveSendStatusEnum(Integer saveNumber,String status){
        this.saveNumber=saveNumber;
        this.status=status;
    }
    public Integer getSaveNumber(){
        return saveNumber;
    }
    public String getStatus(){
        return status;
    }

    private final static Map<Integer, SaveSendStatusEnum> map =
            Arrays.stream(SaveSendStatusEnum.values()).collect(toMap(leg -> leg.getSaveNumber(), leg -> leg));
    public static SaveSendStatusEnum getStatusEnum(Integer integer){
        return map.get(integer);
    }
    @Override
    public String toString() {
        return status;
    }
}
