package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.Result;
import com.kahago.kahagoservice.model.validate.OnBookingTitipanRequest;
import com.kahago.kahagoservice.model.validate.OnDeleteHeaderTitipanRequest;
import com.kahago.kahagoservice.model.validate.OnDetailTitipanRequest;
import com.kahago.kahagoservice.model.validate.OnHeaderTitipanRequest;
import com.kahago.kahagoservice.service.PickupCourierService;
import com.kahago.kahagoservice.service.PickupOnGoingService;
import com.kahago.kahagoservice.service.PickupTitipanService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class PickupTitipanController {

    @Autowired
    private PickupTitipanService pickupTitipanService;

    @Autowired
    private PickupCourierService pickupCourierService;

    @Autowired
    private PickupOnGoingService pickupOnGoingService;

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PutMapping("/courier/titipan/not-booked/{id}")
    public Result processNewItem(@PathVariable Integer id, Authentication authentication) {

        OnHeaderTitipanRequest pickupRequest = OnHeaderTitipanRequest.builder()
                .id(id)
                .courierId(authentication.getName())
                .build();

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), pickupTitipanService.newPickup(pickupRequest));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @DeleteMapping("/courier/titipan/not-booked/{id}/item/{bookId}")
    public Result deleteItem(@PathVariable Integer id, @PathVariable String bookId, Authentication authentication) {
        OnDeleteHeaderTitipanRequest request = OnDeleteHeaderTitipanRequest.builder()
                .id(id)
                .courierId(authentication.getName())
                .bookId(bookId)
                .build();

        pickupTitipanService.deletePickup(request);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PostMapping(value = "/courier/titipan/not-booked/{id}/item/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public Result processItemInNotBooked(@PathVariable Integer id, @PathVariable String bookId, @RequestParam MultipartFile image,
                                         @RequestParam("qr_code") String qrCode, Authentication authentication) {
        OnDetailTitipanRequest request = OnDetailTitipanRequest.builder()
                .bookId(bookId)
                .id(id)
                .courierId(authentication.getName())
                .qrCode(qrCode)
                .image(image)
                .build();

        pickupTitipanService.addDetailPickup(request);

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/titipan/booked/{id}")
    public Result fetchAllById(@PathVariable Integer id) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), pickupTitipanService.getDataBook(id));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/titipan/booked/{id}/item/{idBook}")
    public Result detailOfItem(@PathVariable Integer id, @PathVariable String idBook) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), pickupTitipanService.itemDetails(idBook));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PostMapping("/courier/titipan/booked/{id}/item/{bookId}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public Result processItemInBooked(@PathVariable Integer id, @PathVariable String bookId, @RequestParam MultipartFile image,
                                      @RequestParam("part_id") Integer partId, @RequestParam("qr_code") String qrCode, Principal principal) {

        OnBookingTitipanRequest request = OnBookingTitipanRequest.builder()
                .id(id)
                .bookId(bookId)
                .courierId(principal.getName())
                .image(image)
                .qrCode(qrCode)
                .partId(partId)
                .build();

        pickupTitipanService.doUpdatePickup(request);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }
}
