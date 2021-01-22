package com.kahago.kahagoservice.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Hendro yuwono
 */
@Data
public class CategorySwitcherSaveReq {

    @NotNull
    private Integer switcherCode;
    @NotNull
    private Integer idUserCategory;
}
