package com.kahago.kahagoservice.model.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author bangd ON 27/12/2019
 * @project com.kahago.kahagoservice.model.request
 */
@Data
public class BookingListAssignRequest {
    private String user_id;
    private LocalDate date;
    private Integer pickup_time_id;
}
