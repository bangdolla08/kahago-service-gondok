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
import com.kahago.kahagoservice.service.KebijakanService;
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
@Api(value = "Kebijakan Vendor Management System", description = "Operations to manage Kebijakan Vendor")
public class KebijakanVendorController extends Controller{

	@Autowired
	private KebijakanService kebijakanService;
	
    private static final Logger log = LoggerFactory.getLogger(KebijakanVendorController.class);
    
    
    @ApiOperation(value = "View a list of Kebijakan Vendor")
    @PostMapping("/kebijakan/vendor/open")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getSavePermohonan(@RequestBody PermohonanSaveReq req,Principal principal){
    	log.info("==>Save of Open ===> "+Common.json2String(req));
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                kebijakanService.doOpenPermohonan(req, principal.getName())
                );
    }

    @ApiOperation(value = "View a list of Permohonan Vendor")
    @PostMapping("/kebijakan/vendor/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<PermohonanDetailResp>> getOrigin(@RequestBody PermohonanListReq req){
    	log.info("==>List of Permohonan Vendor<===");
    	log.info("==> Request "+Common.json2String(req));
    	Page<PermohonanDetailResp> lsPermohonan = kebijakanService.getDataKebijakan(req);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                this.extraPaging(lsPermohonan),
                lsPermohonan.getContent());
    }
    
    @ApiOperation(value = "View a list of Permohonan Vendor Detail")
    @PostMapping("/kebijakan/vendor/verifikasi")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> doApprovalRejectPermohonan(@RequestBody PermohonanSaveReq req,Principal principal){
    	log.info("==>Save of Open ===> "+Common.json2String(req));
    	log.info("===> User "+principal.getName());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                kebijakanService.doSaveApprovalReject(req, principal.getName())
                );
    }
    
}
