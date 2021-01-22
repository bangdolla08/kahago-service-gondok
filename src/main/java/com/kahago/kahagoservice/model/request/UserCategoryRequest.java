package com.kahago.kahagoservice.model.request;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.kahago.kahagoservice.model.response.MenuDetails;
import com.kahago.kahagoservice.model.response.MenuTitle;

/**
 * @author Hendro yuwono
 */
@Data
public class UserCategoryRequest {
    @NotBlank
    private String nameCategory;
    @NotBlank
    private String accountType;
    @NotBlank
    private String roleName;
    private Integer idUserCategory;
    private Integer minKoli;
    private Boolean requestOne;
    private Boolean requestTwo;
    private Boolean requestThree;
    private Boolean payLater;
    private Boolean autoResi;
    private List<Integer> pickupTimeId;
    private List<Integer> vendorCode;
    private List<OptionPayment> optionPayment;
    private List<MenuTitle> menuDetails;
    
}
