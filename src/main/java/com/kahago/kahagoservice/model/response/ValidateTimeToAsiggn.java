package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 28/01/2020
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@Builder
public class ValidateTimeToAsiggn {
    private Boolean canDraft;
    private Boolean canAssign;
    private Boolean canSearch;
}
