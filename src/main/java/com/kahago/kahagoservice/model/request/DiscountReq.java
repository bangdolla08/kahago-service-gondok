package com.kahago.kahagoservice.model.request;

import lombok.Data;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@Data
public class DiscountReq {
    private String code;
    private String optionPayment;
    private String userId;
    private List<DetailBooks> books;
}
