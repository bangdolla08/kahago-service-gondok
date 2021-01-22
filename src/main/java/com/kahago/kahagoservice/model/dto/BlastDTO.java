package com.kahago.kahagoservice.model.dto;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ibnu Wasis
 */
@Builder
@Data
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class BlastDTO {
    private String title;
    private String description;
    private String idBlast;
    private String imageLocation;
    private Integer typeBlast;
    private List<Integer> userCategory;
}
