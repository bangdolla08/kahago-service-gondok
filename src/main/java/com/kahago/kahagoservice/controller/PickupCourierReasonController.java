package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.Result;
import com.kahago.kahagoservice.service.ReasonPickupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class PickupCourierReasonController {

    @Autowired
    private ReasonPickupService reasonPickupService;

    @GetMapping("/courier/pickup/reason")
    public Result reasonPickup(@RequestParam(defaultValue = "CANCEL_PICKUP") String category) {

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), reasonPickupService.getAllReason(category.toUpperCase()));
    }
}
