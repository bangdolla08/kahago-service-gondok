package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.entity.MReceiverEntity;
import com.kahago.kahagoservice.entity.MSenderEntity;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.SaveStatusEnum;
import com.kahago.kahagoservice.model.request.AddressListRequest;
import com.kahago.kahagoservice.model.request.AddressRequest;
import com.kahago.kahagoservice.model.request.PickupAddressRequest;
import com.kahago.kahagoservice.model.request.PickupListRequest;
import com.kahago.kahagoservice.model.response.AddressResponse;
import com.kahago.kahagoservice.model.response.PickupAddressResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseWithRequest;
import com.kahago.kahagoservice.service.AddressService;
import com.kahago.kahagoservice.service.AreaService;
import com.kahago.kahagoservice.service.PickupAddressService;
import com.kahago.kahagoservice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 18/11/2019
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Api(value = "Address Management System", description = "View and change data addresses that have been stored")
public class AddressController extends Controller{
    @Autowired
    private PickupAddressService pickupAddressService;

    @Autowired
    private AddressService addressService;
    
    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @ApiOperation(value = "Show Address Pickup ")
    @GetMapping("/address/pickupaddress")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<PickupListRequest, List<PickupAddressResponse>> listPickupAddress(Pageable pageable,
                                                                                                 PickupListRequest pickupListRequest,HttpServletRequest req){
    	log.info("===>Pickup Addres<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        ResponseWithRequest<PickupListRequest, List<PickupAddressResponse>> responseWithRequest=new ResponseWithRequest<>();
      ///Page<PickupAddressResponse> pickupAddressResponses=pickupAddressService.getListPickup(pickupListRequest.getUserId(),pickupListRequest.getOrigin(),pageable);
        List<PickupAddressResponse> pickupAddressResponses = pickupAddressService.getListPickup(pickupListRequest.getUserId(), pickupListRequest.getOrigin());
        responseWithRequest.setRequest(pickupListRequest);
        responseWithRequest.setData(pickupAddressResponses);
        //responseWithRequest.setPage(extraPaging(pickupAddressResponses));
        responseWithRequest.setRc(ResponseStatus.OK.value());
        responseWithRequest.setDescription(ResponseStatus.OK.getReasonPhrase());
        return responseWithRequest;
    }
    @ApiOperation(value = "Add Address Pickup ")
    @PutMapping("/address/pickupaddress")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<PickupAddressResponse>> addPickupAddress(@RequestBody PickupAddressRequest pickupAddressRequest,HttpServletRequest req){
    	log.info("===>Add Address pickup<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        return saveDatePickupAddress(pickupAddressRequest,SaveStatusEnum.SAVE);
    }
    @ApiOperation(value = "Edit Address Pickup ")
    @PostMapping("/address/pickupaddress")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<PickupAddressResponse>> updatePickupAddress(@RequestBody PickupAddressRequest pickupAddressRequest,HttpServletRequest req){
    	log.info("===>Edit Address pickup<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return saveDatePickupAddress(pickupAddressRequest,SaveStatusEnum.EDIT);
    }
    @ApiOperation(value = "Delete Address Pickup ")
    @DeleteMapping("/address/pickupaddress")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<PickupAddressResponse>> deletePickupAddress(PickupAddressRequest pickupAddressRequest,HttpServletRequest req){
    	log.info("===>Delete Address pickup<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return saveDatePickupAddress(pickupAddressRequest,SaveStatusEnum.DELETE);
    }

    @ApiOperation(value = "List Address Sender ")
    @GetMapping("/address/sender")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> getAddressSender(AddressListRequest addressListRequest,HttpServletRequest req){
    	log.info("===>List Address Sender<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        return getAddress(false,addressListRequest);
    }

    @ApiOperation(value = "Add Address Sender ")
    @PutMapping("/address/sender")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> addAddressSender(@RequestBody AddressRequest addressRequest,HttpServletRequest req){
    	log.info("===>Add Address Sender<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        return saveSenderAddress(addressRequest,SaveStatusEnum.SAVE);
    }

    @ApiOperation(value = "Edit Address Sender Most Id Address")
    @PostMapping("/address/sender")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> editAddressSender(@RequestBody AddressRequest addressRequest,HttpServletRequest req){
    	log.info("===>Edit Address Sender Most Id Address<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return saveSenderAddress(addressRequest,SaveStatusEnum.EDIT);
    }

    @ApiOperation(value = "Delete Address Sender Most Id Address")
    @DeleteMapping("/address/sender")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> deleteAddressSender(AddressRequest addressRequest,HttpServletRequest req){
    	log.info("===>Delete Address Sender Most Id Address<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return saveSenderAddress(addressRequest,SaveStatusEnum.DELETE);
    }

    @ApiOperation(value = "List Address Receiver ")
    @GetMapping("/address/receiver")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> getAddressReceiver(AddressListRequest addressListRequest,HttpServletRequest req){
    	log.info("===>List Address Receiver<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return getAddress(true,addressListRequest);
    }

    @ApiOperation(value = "Add Address Receiver ")
    @PutMapping("/address/receiver")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> addAddressReceiver(@RequestBody AddressRequest addressRequest,HttpServletRequest req){
    	log.info("===>Add Address Receiver<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        return saveReceiverAddress(addressRequest,SaveStatusEnum.SAVE);
    }

    @ApiOperation(value = "Edit Address Receiver Most Id Address")
    @PostMapping("/address/receiver")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> editAddressReceiver(@RequestBody AddressRequest addressRequest,HttpServletRequest req){
    	log.info("===>Edit Address Receiver Most Id Address<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return saveReceiverAddress(addressRequest,SaveStatusEnum.EDIT);
    }

    @ApiOperation(value = "Delete Address Receiver Most Id Address")
    @DeleteMapping("/address/receiver")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<AddressResponse>> deleteAddressReceiver(AddressRequest addressRequest,HttpServletRequest req){
    	log.info("===>Delete Address Receiver Most Id Address<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        return saveReceiverAddress(addressRequest,SaveStatusEnum.DELETE);
    }

    @ApiOperation(value = "Get Address By User Id Or userRef")
    @GetMapping("/address/getuser")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<PickupAddressResponse>> getAddress(AddressListRequest addressListRequest,HttpServletRequest req){
    	log.info("===>Get Address By User Id Or userRef<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
        ResponseWithRequest<String,List<PickupAddressResponse>> response = new ResponseWithRequest<>();
            response.setRequest("Receiver");
        response.setRc(ResponseStatus.OK.value());
        response.setDescription(ResponseStatus.OK.getReasonPhrase());
        response.setData(pickupAddressService.getFromUserRef(addressListRequest));
        return response;
    }
    /**
     * To Get Address
     * @param isReceiver Untuk List Penerima
     * @param addressListRequest permintaan Dari Controller
     * @return list Address yang di dapatkan
     */
    private ResponseWithRequest<String,List<AddressResponse>> getAddress(Boolean isReceiver,AddressListRequest addressListRequest){
        ResponseWithRequest<String,List<AddressResponse>> response = new ResponseWithRequest<>();
        if(isReceiver)
            response.setRequest("Receiver");
        else
            response.setRequest("Sender");
        response.setRc(ResponseStatus.OK.value());
        response.setDescription(ResponseStatus.OK.getReasonPhrase());
        if(isReceiver)
            response.setData(addressService.getReceiverAddress(addressListRequest));
        else
            response.setData(addressService.getSenderAddress(addressListRequest));
        return response;
    }

    private Response<List<PickupAddressResponse>> saveDatePickupAddress(PickupAddressRequest pickupAddressRequest,SaveStatusEnum saveStatusEnum){
        Response<List<PickupAddressResponse>> pickupAddressResponseResponse=new Response<>();
        pickupAddressResponseResponse.setRc(ResponseStatus.OK.value());
        pickupAddressResponseResponse.setDescription(ResponseStatus.OK.getReasonPhrase());
        List<PickupAddressResponse> pickupAddressResponses=new ArrayList<>();
        pickupAddressResponses.add(pickupAddressService.savePickup(pickupAddressRequest, saveStatusEnum));
        pickupAddressResponseResponse.setData(pickupAddressResponses);
        return pickupAddressResponseResponse;
    }

    @Autowired
    private AreaService areaService;
    @Autowired
    private UserService userService;

    private ResponseWithRequest<String,List<AddressResponse>> saveSenderAddress(AddressRequest addressRequest,SaveStatusEnum saveStatusEnum){
        ResponseWithRequest<String,List<AddressResponse>> response = new ResponseWithRequest<>();
        MSenderEntity mSenderEntity=addressService.getSender(addressRequest.getId());
        if(SaveStatusEnum.DELETE!=saveStatusEnum) {
            mSenderEntity.setSenderAddress(addressRequest.getAddress());
            mSenderEntity.setSenderEmail(addressRequest.getEmail());
            mSenderEntity.setSenderName(addressRequest.getName());
            mSenderEntity.setStatus(saveStatusEnum.getFlagStatusEnum().getValueInteger());
            mSenderEntity.setSenderTelp(addressRequest.getTelp());
            mSenderEntity.setUserId(userService.get(addressRequest.getUserId()));
            if (addressRequest.getId() != null)
                mSenderEntity.setSenderId(addressRequest.getId());
        }else{
            mSenderEntity.setStatus(saveStatusEnum.getFlagStatusEnum().getValueInteger());
        }
        response.setRequest("Sender");
        response.setRc(ResponseStatus.OK.value());
        response.setDescription(ResponseStatus.OK.getReasonPhrase());
        List<AddressResponse> addressResponses=new ArrayList<>();
        addressResponses.add(addressService.saveSenderAddress(mSenderEntity));
        response.setData(addressResponses);
        return response;
    }

    private ResponseWithRequest<String,List<AddressResponse>> saveReceiverAddress(AddressRequest addressRequest,SaveStatusEnum saveStatusEnum){
        ResponseWithRequest<String,List<AddressResponse>> response = new ResponseWithRequest<>();
        MReceiverEntity mReceiverEntity=addressService.getReceiver(addressRequest.getId());
        if(SaveStatusEnum.DELETE!=saveStatusEnum) {
            mReceiverEntity.setReceiverAddress(addressRequest.getAddress());
            mReceiverEntity.setReceiverEmail(addressRequest.getEmail());
            mReceiverEntity.setIdPostalCode(areaService.getPostalCodeEntity(addressRequest.getIdPostalCode()));
            mReceiverEntity.setReceiverTelp(addressRequest.getTelp());
            mReceiverEntity.setStatus(saveStatusEnum.getFlagStatusEnum().getValueInteger());
            mReceiverEntity.setReceiverName(addressRequest.getName());
            mReceiverEntity.setUserId(userService.get(addressRequest.getUserId()));
        }else
        {
            mReceiverEntity.setStatus(saveStatusEnum.getFlagStatusEnum().getValueInteger());
        }
        response.setRequest("Receiver");
        response.setRc(ResponseStatus.OK.value());
        response.setDescription(ResponseStatus.OK.getReasonPhrase());
        List<AddressResponse> responses=new ArrayList<>();
        responses.add(addressService.saveReceiverAddress(mReceiverEntity));
        response.setData(responses);

        return response;
    }

}
