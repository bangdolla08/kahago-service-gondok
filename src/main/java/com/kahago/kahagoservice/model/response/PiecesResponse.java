package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class PiecesResponse {
    private int piecesId;
    private String receiver;
    private String destination;
    private int quantity;
    private double weight;
    private String qrCodeExt;
    private String pathImagePieces;
    private String pathImageVendor;
    private String product;
    private String status;
}
