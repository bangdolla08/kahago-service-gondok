package com.kahago.kahagoservice.model.response;

import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
public class PiecesOfItemResponse {
    private String bookId;
    private String receiver;
    private Integer count;
    private String destination;
    private Integer weight;
    private String vendor;
    private String imageVendor;
    private String product;
}
