package com.kahago.kahagoservice.model.request;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;

/**
 * @author Hendro yuwono
 */
@Data
public class EditTutorialRequest {
    private Integer step;
    private Integer typeOfTutorial;
    private Boolean showInDashboard;
    private String description;
    private String promoName;
    private String imageFront;
    private String imageBackground;
    private String imagePopup;
    public byte[] getByteImageFront() {
        return Base64.decodeBase64(imageFront);
    }

    public byte[] getByteImageBackground() {
        return Base64.decodeBase64(imageBackground);
    }

    public byte[] getByteImagePopup() {
        return Base64.decodeBase64(imagePopup);
    }
}
