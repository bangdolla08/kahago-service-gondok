package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.IncomingCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class IncomingCounterController {

    @Autowired
    private IncomingCounterService counterService;

    @GetMapping("/counter/incoming/{bookingCode}")
    public Response<SaveResponse> incoming(@PathVariable String bookingCode, Principal principal, String officeCode) {

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                counterService.incoming(principal.getName(), officeCode, bookingCode));

    }

}
