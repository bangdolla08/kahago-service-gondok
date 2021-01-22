package com.kahago.kahagoservice.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bangd ON 31/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@JsonSerialize
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class SaveResponse {
    private Integer saveStatus;
    private String saveInformation;
    private String linkResi;
    private String noResi;
    private String messageDialog;
    private List<RespUncomplete> results;
}
