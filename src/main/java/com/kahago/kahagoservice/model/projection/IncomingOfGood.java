package com.kahago.kahagoservice.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class IncomingOfGood {
    private String qrCode;
    private String nameOfCustomer;
    private String nameOfCourier;
    private String lengthOfVolume;
    private String widthOfVolume;
    private String heightOfVolume;
    private String volume;
    private String weight;
    private String product;
    private String vendor;
    private Integer status;
    private Boolean normalBook;
    private String bookingCode;

}
