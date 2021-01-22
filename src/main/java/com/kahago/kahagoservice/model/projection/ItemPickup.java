package com.kahago.kahagoservice.model.projection;

import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.util.DateTimeUtil;
import com.kahago.kahagoservice.util.ImageConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
public class ItemPickup {
    private String idBooking;
    private Integer idPickupDetail;
    private String sender;
    private String receiver;
    private String receiverAddress;
    private Integer totalItem;
    private Long weight;
    private Integer statusPickDetail;
    private String pathImage;
    private boolean isRequestPickup;
    private Integer idPickOrderReqDetail;
    private String pickupTime;

    public String getSender() {
        return Strings.isEmpty(sender) ? "-" : sender;
    }

    public String getReceiver() {
        return Strings.isEmpty(receiver) ? "-" : receiver;
    }

    public String getReceiverAddress() {
        return Strings.isEmpty(receiverAddress) ? "-" : receiverAddress;
    }

    public Long getWeight() {
        return Objects.isNull(weight) ? 0 : weight;
    }

    public String getPathImage() {
        return Strings.isEmpty(pathImage) ? "-" : ImageConstant.reversePathVendorToUrl(pathImage);
    }

    public String keyOfStatus() {
        return PickupDetailEnum.byValue(statusPickDetail).getKey();
    }

    public boolean hasPieces() {
        return !Objects.isNull(idPickOrderReqDetail);
    }

    public String getPickupTime() {
        String[] splitTime = this.pickupTime.split(",");
        LocalDate pickupDate = LocalDate.parse(splitTime[0]);
        String[] rangeOfTime = splitTime[1].replace(" ", "").split("-");

        String timeFrom = rangeOfTime[0].substring(0, 5);
        String timeTo = rangeOfTime[1].substring(0, 5);
        return dateOfDay(pickupDate) +", "+ timeFrom +" "+ timeTo;
    }

    private String dateOfDay(LocalDate pickupDate) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        if (pickupDate.equals(LocalDate.now())){
            return "Hari ini";
        } else if (pickupDate.equals(LocalDate.now().minusDays(1))) {
            return "Kemarin";
        } else if (pickupDate.equals(LocalDate.now().plusDays(1))){
            return "Besok";
        } else {
            return pickupDate.format(formatterDate);
        }
    }
}
