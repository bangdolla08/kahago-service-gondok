package com.kahago.kahagoservice.model.projection;

import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class PiecesOfItem {
    private Integer id;
    private String receiver;
    private String district;
    private String city;
    private Integer quantity;
    private Double weight;
    private String qrCodeExt;
    private String pathImagePieces;
    private String pathImageVendor;
    private String product;
    private Integer status;


    public String getDestination() {
        if (Strings.isEmpty(this.district) || Strings.isEmpty(this.city)) {
            return "-";
        }
        return this.district +", "+ this.city;
    }

    public String getKeyStatus() {
        return RequestPickupEnum.getByValue(this.getStatus()).getKey();
    }

    public String getReceiver() {
        if (Strings.isEmpty(this.receiver)) {
            return "-";
        }
        return this.receiver;
    }

    public double getWeight() {
        if (Objects.isNull(this.weight)) {
            return 0;
        }
        return this.weight;
    }

    public String getPathImageVendor() {
        if (Strings.isEmpty(this.pathImageVendor)) {
            return "";
        }
        return this.pathImageVendor;
    }

    public String getPathImagePieces() {
        if (Strings.isEmpty(this.pathImagePieces)) {
            return "";
        }
        return this.pathImagePieces;
    }

    public String getProduct() {
        if (Strings.isEmpty(this.product)) {
            return "-";
        }
        return this.product;
    }
}
