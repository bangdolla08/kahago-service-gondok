package com.kahago.kahagoservice.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class PickupCourier {
    private Integer id;
    private Integer pickupId;
    private Integer status;
    private LocalTime pickupTimeFrom;
    private LocalTime pickupTimeTo;
    private LocalDate pickupDate;
    private String manifest;
    private String name;
    private String phone;
    private Integer pickupAddressId;
    private String address;
    private String description;
    private String latitude;
    private String longitude;
    private int firstTimeVisiting;


    public String getDescription() {
        return this.description == null ? "-" : this.description;
    }

    public Double getLatitude() {
        if (Strings.isEmpty(this.latitude)) {
            return 0D;
        }

        return Double.valueOf(this.latitude);
    }

    public Double getLongitude() {
        if (Strings.isEmpty(this.longitude)) {
            return 0D;
        }

        return Double.valueOf(this.longitude);
    }

    public String dateOfAssign() {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");


        String times = this.pickupTimeFrom.format(formatterTime) + " - " + this.pickupTimeTo.format(formatterTime);
        return dateOfDay() + ", " + times;
    }

    private String dateOfDay() {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        if (this.pickupDate.equals(LocalDate.now())){
            return "Hari ini";
        } else if (this.pickupDate.equals(LocalDate.now().minusDays(1))) {
            return "Kemarin";
        } else {
            return this.pickupDate.format(formatterDate);
        }
    }

    public Boolean getFirstTimeVisiting() {
        return this.firstTimeVisiting == 0;
    }

    public Double[] getCoordinate() {
        return new Double[]{getLongitude(), getLatitude()};
    }
}
