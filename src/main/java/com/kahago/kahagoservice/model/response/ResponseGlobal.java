package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Hendro yuwono
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class ResponseGlobal {
    private String rc;
    private String description;
}
