package com.kahago.kahagoservice.model.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class ItemPCP {
	@NotNull
    private Integer seq;
    @NotNull
    private Double actualWeight;
    @NotNull
    private Integer height;
    @NotNull
    private Integer width;
    @NotNull
    private Integer length;
    @NotNull
    private Integer volume;
}
