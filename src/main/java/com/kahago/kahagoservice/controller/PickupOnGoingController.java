package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.UpdatePickupStatusRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.Result;
import com.kahago.kahagoservice.model.validate.*;
import com.kahago.kahagoservice.service.PickupOnGoingService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class PickupOnGoingController extends Controller {

    @Autowired
    private PickupOnGoingService pickupOnGoingService;

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/ongoing")
    public Result onProcess(Authentication authentication) {

        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                pickupOnGoingService.getOnProcess(authentication.getName()));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PutMapping("/courier/ongoing/{id}")
    public Result modifyStatus(@PathVariable Integer id, @RequestBody UpdatePickupStatusRequest pickupReq,
                               Authentication authentication) {
        OnGoingPickupRequest statPickReq = OnGoingPickupRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .status(pickupReq.getStatus())
                .reason(pickupReq.getReason())
                .build();

        pickupOnGoingService.updateOnProcessCourier(statPickReq);
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/ongoing/{id}/item")
    public Result detailManifest(@PathVariable Integer id, Authentication authentication) {

        OnItemPickupRequest detailPickup = OnItemPickupRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .build();

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pickupOnGoingService.getItems(detailPickup));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/ongoing/{id}/item/{idBook}")
    public Result itemOfDetailManifest(@PathVariable Integer id, @PathVariable String idBook,
                                       Authentication authentication) {

        OnItemDetailPickupRequest itemDetail = OnItemDetailPickupRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .bookId(idBook)
                .build();

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pickupOnGoingService.getItemDetails(itemDetail));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PutMapping("/courier/ongoing/{id}/item/{idBook}")
    public Result rejectPickup(@PathVariable Integer id, @PathVariable String idBook,
                               @RequestBody UpdatePickupStatusRequest statusRequest, Authentication authentication) {

        OnCancelPickingRequest processPickup = OnCancelPickingRequest.builder()
                .courierId(authentication.getName())
                .id(id)
                .bookId(idBook)
                .status(statusRequest.getStatus())
                .reason(statusRequest.getReason())
                .build();

        pickupOnGoingService.rejectPickup(processPickup);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PostMapping(value = "/courier/ongoing/{id}/item/{idBook}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public Result processPickupInNormalBooking(@PathVariable Integer id, @PathVariable String idBook,
                                               @RequestParam MultipartFile image, @RequestParam("qr_code") String qrCode,
                                               @RequestParam("part_id") Integer partId, Authentication authentication) {

        OnAcceptPickBookRequest request = OnAcceptPickBookRequest.builder()
                .id(id)
                .bookId(idBook)
                .courierId(authentication.getName())
                .qrCode(qrCode)
                .partId(partId)
                .image(image)
                .build();

        pickupOnGoingService.acceptBooking(request);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/courier/ongoing/{id}/item/{idBook}/pieces")
    public Result piecesOfDetailManifest(@PathVariable Integer id, @PathVariable String idBook, Authentication authentication) {

        OnAcceptPickBookRequest request = OnAcceptPickBookRequest.builder()
                .id(id)
                .bookId(idBook)
                .courierId(authentication.getName())
                .build();

        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(),
                pickupOnGoingService.getPieces(request));
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @PostMapping(value = "/courier/ongoing/{id}/item/{idBook}/pieces",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public Result processPickupInRequestPickup(@PathVariable Integer id, @PathVariable String idBook,
                                               @RequestParam MultipartFile image, @RequestParam("qr_code") String qrCode,
                                               @RequestParam(value = "pieces_id", required = false) Integer piecesId, Authentication authentication) {

        OnAcceptPickReqPickupRequest request = OnAcceptPickReqPickupRequest.builder()
                .id(id)
                .bookId(idBook)
                .courierId(authentication.getName())
                .qrCode(qrCode)
                .piecesId(piecesId)
                .image(image)
                .build();

        pickupOnGoingService.acceptRequestPickup(request);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }
}
