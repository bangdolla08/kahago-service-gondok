package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class TutorialBoResponse {
    private Integer id;
    private Integer step;
    private Integer typeOfTutorial;
    private boolean showInDashboard;
    private String description;
    private String promoName;
    private String pathImageFront;
    private String pathImageBackground;
    private String pathImagePopup;
    private String lastUpdate;
    private String lastModifyBy;
}
