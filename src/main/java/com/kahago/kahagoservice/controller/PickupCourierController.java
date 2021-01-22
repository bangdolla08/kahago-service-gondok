package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.PickupResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.request.UpdatePickupStatusRequest;
import com.kahago.kahagoservice.model.request.UpdateStatusPickupRequest;
import com.kahago.kahagoservice.model.response.Result;
import com.kahago.kahagoservice.model.validate.*;
import com.kahago.kahagoservice.service.PickupCourierService;

import com.kahago.kahagoservice.service.PickupOnGoingService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.web.bind.annotation.*;

/**
 * @author Hendro yuwono
 */
@Slf4j
@BaseController
@ResponseBody
public class PickupCourierController extends Controller {

    @Autowired
    private PickupCourierService pickupCourierService;

    @Autowired
    private PickupOnGoingService pickupOnGoingService;

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/pickup/ready-pickup")
    public Result fetchAllInReady(@RequestParam(value = "time_pickup", required = false) Integer[] timePickup,
                                  @RequestParam(required = false) String term, Authentication authentication, Pageable pageable) {


        FetchPickupRequest request = FetchPickupRequest.builder()
                .courierId(authentication.getName())
                .timePickup(timePickup == null ? new ArrayList<>() : Arrays.asList(timePickup))
                .term(term == null ? "" : term)
                .keyOfStatus("READY_PICKUP")
                .pageable(pageable)
                .build();

        Page<PickupResponse> dataPickup = pickupCourierService.getPickup(request);

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pagination(dataPickup), dataPickup.getContent());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PutMapping("/courier/pickup/ready-pickup/{id}")
    public Result processStatusInReady(@PathVariable Integer id, @RequestBody UpdatePickupStatusRequest pickupReq,
                                       Authentication authentication) {

        OnReadyPickupRequest statPickReq = OnReadyPickupRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .status(pickupReq.getStatus())
                .build();

        pickupCourierService.updateOnGoingCourier(statPickReq);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/pickup/ready-pickup/{id}")
    public Result detailManifestInReady(@PathVariable Integer id, Authentication authentication) {

        OnItemPickupRequest detailPickup = OnItemPickupRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .build();

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pickupOnGoingService.getItems(detailPickup));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/pickup/finish-pickup")
    public Result fetchAllInFinish(@RequestParam(value = "time_pickup", required = false) Integer[] timePickup,
                                   @RequestParam(required = false) String term, Authentication authentication, Pageable pageable) {

        FetchPickupRequest request = FetchPickupRequest.builder()
                .courierId(authentication.getName())
                .timePickup(timePickup == null ? new ArrayList<>() : Arrays.asList(timePickup))
                .term(term == null ? "" : term)
                .keyOfStatus("FINISH_PICKUP")
                .pageable(pageable)
                .build();

        Page<PickupResponse> dataPickup = pickupCourierService.getPickup(request);

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pagination(dataPickup), dataPickup.getContent());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/pickup/finish-pickup/{id}")
    public Result detailManifestInFinish(@PathVariable Integer id, Authentication authentication) {

        OnItemPickupRequest detailPickup = OnItemPickupRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .build();

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pickupOnGoingService.getItems(detailPickup));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PutMapping("/courier/pickup/finish-pickup")
    public Result processToWarehouse(@RequestBody UpdateStatusPickupRequest pickupReq, Authentication authentication) {

        OnFinishPickupRequest onFinish = OnFinishPickupRequest.builder()
                .courierId(authentication.getName())
                .status(pickupReq.getStatus())
                .build();

        pickupCourierService.processToWarehouse(onFinish);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PutMapping("/courier/pickup/otw-warehouse")
    public Result cancelToWarehouse(@RequestBody UpdateStatusPickupRequest pickupReq, Authentication authentication) {

        OnWarehousePickupRequest onWarehouse = OnWarehousePickupRequest.builder()
                .status(pickupReq.getStatus())
                .courierId(authentication.getName())
                .build();

        pickupCourierService.cancelToWarehouse(onWarehouse);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }
}
