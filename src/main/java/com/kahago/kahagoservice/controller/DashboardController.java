package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.ModaResponse;
import com.kahago.kahagoservice.model.response.OfficeCodeResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseContactPerson;
import com.kahago.kahagoservice.model.response.VendorResponse;
import com.kahago.kahagoservice.service.ModaService;
import com.kahago.kahagoservice.service.OfficeCodeService;
import com.kahago.kahagoservice.service.VendorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 05/12/2019
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Api(value = "Dashboard management")
public class DashboardController {

    @Value("${helpDesk.Wa}")
    private String helpDeskWa;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private OfficeCodeService officeService;
    @Autowired
    private ModaService modaService;
    
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("/dashboard/contactperson/wa")
    public Response<ResponseContactPerson> getWhatsApp(HttpServletRequest req){
    	log.info("==>contact person<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        ResponseContactPerson responseContactPerson= ResponseContactPerson.builder().helpDeskNumber(helpDeskWa).build();
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),responseContactPerson);
    }

    @ApiOperation(value="List Of Vendor")
    @GetMapping("/dashboard/vendors")
    public Response<List<VendorResponse>> getAll(HttpServletRequest req){
    	log.info("==>vendors<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                vendorService.getAll(false)
        );
    }
    
    @ApiOperation(value="List Of Vendor")
    @GetMapping("/dashboard/vendors/all")
    public Response<List<VendorResponse>> getAllOfThem(HttpServletRequest req){
    	log.info("==>vendors<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                vendorService.getAll(true)
        );
    }
    @ApiOperation(value="List Of Office Code")
    @GetMapping("/dashboard/officecode")
    public Response<List<OfficeCodeResponse>> getAllOffice(HttpServletRequest req){
    	log.info("==>Office Code<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                officeService.getAll()
        );
    }
    
    @ApiOperation(value="List Of Product Switcher Code")
    @GetMapping("/dashboard/productswcode")
    public Response<List<VendorResponse>> getAllProductSwCode(HttpServletRequest req){
    	log.info("==>Office Code<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                vendorService.getAllProduct()
        );
    }
    
    @ApiOperation(value="List of Moda")
    @GetMapping("/moda/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ModaResponse>> getAllListModaActive(){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			modaService.getListModa()
    			);
    }
}
