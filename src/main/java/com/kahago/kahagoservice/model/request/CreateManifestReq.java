package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kahago.kahagoservice.enummodel.SaveSendStatusEnum;
import lombok.Data;

import java.util.List;

/**
 * @author bangd ON 18/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateManifestReq {
    private String userId;
    private String manifestCode;
    private String userIdCorier;
    private Integer timePickupId;
    private List<CreateDetailManifestReq> detail;
}
