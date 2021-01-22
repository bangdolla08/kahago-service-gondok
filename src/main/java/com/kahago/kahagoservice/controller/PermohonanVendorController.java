package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PermohonanListReq;
import com.kahago.kahagoservice.model.request.PermohonanReq;
import com.kahago.kahagoservice.model.request.PermohonanSaveReq;
import com.kahago.kahagoservice.model.response.KecamatanResponse;
import com.kahago.kahagoservice.model.response.KelurahanResponse;
import com.kahago.kahagoservice.model.response.OriginResponse;
import com.kahago.kahagoservice.model.response.PermohonanDetailResp;
import com.kahago.kahagoservice.model.response.PermohononanListRespon;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.AreaService;
import com.kahago.kahagoservice.service.PermohonanService;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.validation.UserMustExist;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;

import java.security.Principal;
import java.util.List;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 8 Jun 2020
 */
@BaseController
@ResponseBody
@Api(value = "PV Management System", description = "Operations to manage Permohonan Vendor")
public class PermohonanVendorController extends Controller{

	@Autowired
	private PermohonanService permohonanService;
	
    private static final Logger log = LoggerFactory.getLogger(PermohonanVendorController.class);
    
    @ApiOperation(value = "View a list of Permohonan Vendor")
    @PostMapping("/permohonan/vendor/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<PermohononanListRespon>> getOrigin(@RequestBody PermohonanListReq req){
    	log.info("==>List of Permohonan Vendor<===");
    	log.info("==> Request "+Common.json2String(req));
    	Page<PermohononanListRespon> lsPermohonan = permohonanService.getAllListPermohonan(req);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                this.extraPaging(lsPermohonan),
                lsPermohonan.getContent());
    }
    
    @ApiOperation(value = "View a list of Permohonan Vendor Detail")
    @GetMapping("/permohonan/vendor/list/{noPermohonan}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<PermohononanListRespon> getListPermohonan(@PathVariable String noPermohonan,Principal principal){
    	log.info("==>List of No Permohonan ===> "+noPermohonan);
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                permohonanService.getDetailPermohonan(noPermohonan,principal.getName()));
    }
    @ApiOperation(value = "Delete a list of Permohonan Vendor Detail")
    @DeleteMapping("/permohonan/vendor/del/{idPermohonan}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getDelPermohonan(@PathVariable Integer idPermohonan,Principal principal){
    	log.info("==>Delete of  ===> "+idPermohonan);
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                permohonanService.doDeletePermohonan(idPermohonan,principal.getName()));
    }

    @ApiOperation(value = "View a list of Permohonan Vendor Detail Books")
    @PostMapping("/permohonan/vendor/list/book")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<PermohonanDetailResp>> getDataBook(@RequestBody PermohonanReq req,Principal principal){
    	log.info("==>List of Book ===> "+Common.json2String(req));
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                permohonanService.getDataBook(req)
                );
    }
    
    @ApiOperation(value = "View a list of Permohonan Vendor Detail")
    @PostMapping("/permohonan/vendor/save")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getSavePermohonan(@RequestBody PermohonanSaveReq req,Principal principal){
    	log.info("==>Save of Book ===> "+Common.json2String(req));
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                permohonanService.doSavePermohonan(req,principal.getName()));
    }
    @ApiOperation(value = "Printing Permohonan")
    @PutMapping("/permohonan/vendor/print/{noPermohonan}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getPrint(@PathVariable String noPermohonan,Principal principal){
    	log.info("==>Print of Nomor Permohonan ===> "+noPermohonan);
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                permohonanService.doPrinting(noPermohonan, principal.getName()));
    }
    

}
