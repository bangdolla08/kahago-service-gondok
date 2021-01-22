package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class PromoRes {

    private Integer seqId;
    private Integer step;
    private String urlImage;
    private String urlImageDetail;
    private String description;
    private Integer typeDasboard;
    private String urlImageBlast;

}
