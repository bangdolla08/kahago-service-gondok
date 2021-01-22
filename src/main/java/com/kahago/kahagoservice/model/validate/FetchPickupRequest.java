package com.kahago.kahagoservice.model.validate;

import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FetchPickupRequest {

    private String term;
    private List<Integer> timePickup;
    private String keyOfStatus;
    private Pageable pageable;
    private String courierId;

    public void setTimePickup(List<Integer> timePickup) {
        if (this.timePickup == null) {
            this.timePickup = new ArrayList<>();
        }
        this.timePickup = timePickup;
    }

    public boolean isSearch() {
        return !term.isEmpty();
    }

    public boolean isFilter() {
        return !timePickup.isEmpty();
    }

    public boolean useSearchAndFilter() {
//        return isSearch() && isFilter();

        if (isSearch()) {
            return true;
        } else return isFilter();
    }

    public List<Integer> getStatusRequest() {
        if (this.keyOfStatus.equalsIgnoreCase("FINISH_PICKUP")) {
            return PickupCourierEnum.showInFinishPickup();
        }
        return PickupCourierEnum.showInReadyPickup();
    }
}
