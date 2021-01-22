package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PriceListRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.VendorArea;
import com.kahago.kahagoservice.model.response.VendorAreaDetail;
import com.kahago.kahagoservice.service.PriceVendorService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * @author bangd
 */
@BaseController
@ResponseBody
@Slf4j
public class PriceVendorController extends Controller {
    @Autowired
    private PriceVendorService priceVendorService;

    @GetMapping("pricevendor/listPostalCode")
    @ApiOperation("List postal code untuk mendapatkan data yang akan di kirim untuk coron")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorArea>> getListVendorByPostalCode(PriceListRequest request, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        Page<VendorArea> response = priceVendorService.getAreaVendor(request);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(response),
                response.getContent()
        );
    }
    @GetMapping("pricevendor/listVendorArea")
    @ApiOperation("List postal code untuk mendapatkan Vendor Area nya")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorAreaDetail>> getListVendorArea(Integer postalCodeId, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        List<VendorAreaDetail> response = priceVendorService.getListVendorRequest(postalCodeId);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                response
        );
    }

    @GetMapping("pricevendor/listPostalCodeProblem")
    @ApiOperation("List postal code dan mvendor area yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorAreaDetail>> getListVendorByPostalCodeProblem(PriceListRequest request, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        Page<VendorAreaDetail> response = priceVendorService.getProblemListVendor(request);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(response),
                response.getContent()
        );
    }

    @GetMapping("masterarea/listprovince")
    @ApiOperation("List provinsi area yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorArea>> getListProvinci(HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.getProvinceList()
        );
    }


    @GetMapping("masterarea/listCity")
    @ApiOperation("List City area yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorArea>> getCity(Integer provId,HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.getCityList(null)
        );
    }
    @GetMapping("masterarea/listKecamantan")
    @ApiOperation("List kecamatan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorArea>> getKecamatan(Integer cityId,HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.getKecamatanList(null)
        );
    }
    @GetMapping("masterarea/listKelurahan")
    @ApiOperation("List kelurahan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorArea>> getKelurahan(Integer kecamatanId,HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.getKelurahanList(null)
        );
    }
    @GetMapping("masterarea/getLateSync")
    @ApiOperation("List kelurahan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorAreaDetail>> getKelurahan(PriceListRequest request, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        Page<VendorAreaDetail> response = priceVendorService.getLateSyncArea(request.getPageRequest());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(response),
                response.getContent()
        );
    }
    @GetMapping("masterarea/getCount")
    @ApiOperation("List kelurahan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> get(HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.getCount()
        );
    }
    @PostMapping("masterarea/saveArea")
    @ApiOperation("List kelurahan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setAreaData(@RequestBody VendorArea request,Principal principal, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.saveArea(request,principal.getName())
        );
    }
    @PostMapping("masterarea/saveVendorArea")
    @ApiOperation("List kelurahan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setVendorAreaData(@RequestBody VendorAreaDetail request, Principal principal, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                priceVendorService.saveRequestToVendor(request,principal.getName())
        );
    }
    @PostMapping("masterarea/deactiveVendorArea")
    @ApiOperation("List kelurahan yang bersamalah di filter melalui status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deactiveVendorArea(@RequestBody VendorAreaDetail request, Principal principal, HttpServletRequest req) {
        return new Response<>(
            ResponseStatus.OK.value(),
            ResponseStatus.OK.getReasonPhrase(),
            priceVendorService.deactiveRequestToVendor(request,principal.getName())
        );
    }
    @PostMapping("masterarea/updateArea")
    @ApiOperation("Update List Kelurahan")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setUpdateData(@RequestBody VendorAreaDetail request, Principal principal, HttpServletRequest req) {
        log.info("===>List Approval Booking<===");
        log.info("device ==> " + req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                    priceVendorService.saveUpdateArea(request,principal.getName())
            );
    }
    
    @PostMapping("masterarea/update/vendorArea")
    @ApiOperation("Update Area By idPostalCode And Vendor")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> updateAreaByIdPostalCodeAndVendor(@RequestBody VendorAreaDetail request,HttpServletRequest req){
    	log.info("==> Update Arae By idPostalCode And Vendor");
    	log.info("device ==> " + req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			priceVendorService.saveUpdateAreaByIdPostalCodeAndVendor(request)
    			);
    }
    

}
