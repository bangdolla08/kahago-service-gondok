package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.OriginsV1Res;
import com.kahago.kahagoservice.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hendro yuwono
 */
@RestController
public class V1HelperController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/origins")
    public OriginsV1Res findOrigin() {
        return OriginsV1Res.builder()
                .rc(ResponseStatus.OK.value())
                .description(ResponseStatus.OK.getReasonPhrase())
                .origins(areaService.getOriginV1())
                .build();
    }
}
