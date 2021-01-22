package com.kahago.kahagoservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.data.domain.Pageable;

/**
 * @author bangd ON 03/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
@JsonSerialize
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HistoryBookRequest {
    private String userId;
    private String startDate;
    private String endDate;
    private Integer filterBy;
    private String idSearch;
    private String cari;
    private Pageable pageable;
}
