package com.kahago.kahagoservice.model.request;

import lombok.Data;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@Data
public class UpdateStatusPickupRequest {
    private String status;
    private List<Integer> ids;
}
