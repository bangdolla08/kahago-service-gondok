package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PickupOrderPriceReq;
import com.kahago.kahagoservice.model.request.PriceRequest;
import com.kahago.kahagoservice.model.response.PriceResponse;
import com.kahago.kahagoservice.model.response.ResponseWithRequest;
import com.kahago.kahagoservice.service.PriceService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author bangd
 */
@BaseController
@ResponseBody
public class PriceController {

    @Autowired
    private PriceService priceService;
    
    private static final Logger log = LoggerFactory.getLogger(PriceController.class);
    @ApiOperation(value = "Get List Price from Origin Selected List To Destination Selected",
            notes = "Beserta Vendor apa saja yang terdapat didalamnya")
    @ApiResponses(value={@ApiResponse(code= 200,message="Price List Details Retrieved")})
    @PostMapping(value = "/checktarif")
    public ResponseWithRequest<PriceRequest,PriceResponse> getPrice(@RequestBody PriceRequest priceRequest,HttpServletRequest req){
    	log.info("==>cek tarif <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("==> Request: "+Common.json2String(priceRequest));
        ResponseWithRequest<PriceRequest,PriceResponse> priceResponse=new ResponseWithRequest<>();
        if(priceRequest.getWeight()==null)
            priceRequest.setWeight(1);
        priceResponse.setRequest(priceRequest);
        priceResponse.setData(priceService.findPrice(priceRequest));
        priceResponse.setRc(ResponseStatus.OK.value());
        priceResponse.setDescription(ResponseStatus.OK.getReasonPhrase());
        return priceResponse;
    }

    @ApiOperation(value = "Get List Price from Origin Selected List To Destination Selected",
            notes = "Beserta Vendor apa saja yang terdapat didalamnya")
    @ApiResponses(value={@ApiResponse(code= 200,message="Price List Details Retrieved")})
    @PostMapping(value = "/checktarif/{idAlamatPickup}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<PriceRequest,PriceResponse> getPriceFromAlamatPickup(@PathVariable Integer idAlamatPickup, @RequestBody PriceRequest priceRequest,HttpServletRequest req){
    	log.info("==>cek tarif by id alamat pickup <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<PriceRequest,PriceResponse> priceResponse=new ResponseWithRequest<>();
        if(priceRequest.getWeight()==null)
            priceRequest.setWeight(1);
        priceResponse.setRequest(priceRequest);
        priceResponse.setData(priceService.findPrice(priceRequest,idAlamatPickup));
        priceResponse.setRc(ResponseStatus.OK.value());
        priceResponse.setDescription(ResponseStatus.OK.getReasonPhrase());
        return priceResponse;
    }

    @ApiOperation(value = "Get List Price for pickup request")
    @ApiResponses(value={@ApiResponse(code= 200,message="Price List Details Retrieved")})
    @PostMapping(value = "/checktarif/pickup")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<PickupOrderPriceReq,PriceResponse> getPriceForPickup(@RequestBody PickupOrderPriceReq priceRequest,
    		HttpServletRequest req,Principal principal){
    	log.info("==>cek tarif by id alamat pickup <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<PickupOrderPriceReq,PriceResponse> priceResponse=new ResponseWithRequest<>();
        if(priceRequest.getWeight()==null)
            priceRequest.setWeight(1);
        priceResponse.setRequest(priceRequest);
        priceResponse.setData(priceService.getTarifPickupRequest(priceRequest,principal.getName()));
        priceResponse.setRc(ResponseStatus.OK.value());
        priceResponse.setDescription(ResponseStatus.OK.getReasonPhrase());
        return priceResponse;
    }
}
