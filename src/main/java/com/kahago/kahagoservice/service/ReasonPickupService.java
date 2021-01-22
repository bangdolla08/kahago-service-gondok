package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MReasonPickupEntity;
import com.kahago.kahagoservice.model.response.ReasonPickupResponse;
import com.kahago.kahagoservice.repository.MReasonPickupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Service
public class ReasonPickupService {

    @Autowired
    private MReasonPickupRepository reasonPickupRepository;

    public List<ReasonPickupResponse> getAllReason(String category) {
        MReasonPickupEntity.Category cat;
        if (category.equals("CANCEL_PICKUP")) {
            cat = MReasonPickupEntity.Category.CANCEL_IN_PICKUP;
        } else {
            cat = MReasonPickupEntity.Category.REJECT_IN_PICKUP;
        }

        return reasonPickupRepository.findByCategory(cat).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ReasonPickupResponse toResponse(MReasonPickupEntity entity) {
        return ReasonPickupResponse.builder()
                .description(entity.getDescription())
                .build();
    }
}
