package com.kahago.kahagoservice.enummodel;

/**
 * @author bangd ON 19/11/2019
 * @project com.kahago.kahagoservice.enummodel
 */
public enum SaveStatusEnum {
    SAVE("Save",FlagStatusEnum.ACTIVE),
    EDIT("Edit",FlagStatusEnum.ACTIVE),
    DELETE("Delete",FlagStatusEnum.NON_ACTIVE);
    private String saveTitle;
    private FlagStatusEnum flagStatusEnum;
    SaveStatusEnum(String saveTitle,FlagStatusEnum flagStatusEnum){
        this.saveTitle=saveTitle;
        this.flagStatusEnum=flagStatusEnum;
    }

    public String getSaveTitle() {
        return saveTitle;
    }

    public FlagStatusEnum getFlagStatusEnum() {
        return flagStatusEnum;
    }
}
