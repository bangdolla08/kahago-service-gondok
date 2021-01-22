package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.validation.TutorialStepperIsExist;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
@JsonSerialize
@TutorialStepperIsExist
public class NewTutorialRequest implements TutorialStepperIsExist.OnProcess {
    @NotNull
    private Integer step;
    @NotNull
    private Integer typeOfTutorial;
    @NotNull
    private boolean showInDashboard;
    @NotNull
    private String description;
    @NotEmpty
    private String promoName;
    @NotEmpty
    private String imageFront;
    @NotEmpty
    private String imageBackground;
    @NotEmpty
    private String imagePopup;

    @JsonIgnore
    public byte[] getByteImageFront() {
        return Base64.decodeBase64(imageFront);
    }

    @JsonIgnore
    public byte[] getByteImageBackground() {
        return Base64.decodeBase64(imageBackground);
    }

    @JsonIgnore
    public byte[] getByteImagePopup() {
        return Base64.decodeBase64(imagePopup);
    }
}
