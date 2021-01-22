package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class MonitorTransResponse {
    private String resultBy;
    private ArrayList<String> resultOrigin;
    private ArrayList<ArrayList<Long>> resultValue;
    private List<Long> resultTotal;
}
