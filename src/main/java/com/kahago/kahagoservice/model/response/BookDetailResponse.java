package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author bangd ON 30/12/2019
 * @project com.kahago.kahagoservice.model.response
 */
@Data
@JsonSerialize
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@Builder
public class BookDetailResponse {
    private Double length;
    private Double width;
    private Double height;
    private Double grossWeight;
    private Double volWeight;
}
