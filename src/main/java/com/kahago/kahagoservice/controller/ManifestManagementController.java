package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.ManifestMoveReq;
import com.kahago.kahagoservice.model.response.ManifestListResp;
import com.kahago.kahagoservice.model.response.ManifestManagementService;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseWithRequest;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BaseController
@ResponseBody
@Api(value="Manifest Management", description="Operating about Manifest ")
public class ManifestManagementController extends Controller{
	@Autowired
	private ManifestManagementService manManService;
	@ApiOperation(value="List of Manifest")
	@GetMapping("/manifest/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ManifestListResp>> getList(String originCityId,String noManifest,Principal principal){
		log.info("GET LIST Manifest Management => "+principal.getName());
		log.info("ORIGIN => "+originCityId);
		return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                manManService.getListManifest(originCityId,noManifest));
	}
	
	@ApiOperation(value="Moving/Create Manifest")
	@PostMapping("/manifest/move")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public ResponseWithRequest<ManifestMoveReq, SaveResponse> doUpdateManifest(@RequestBody ManifestMoveReq req,Principal principal){
		log.info("==> Moving Response <== "+principal.getName());
		log.info("Request => "+Common.json2String(req));
		return new ResponseWithRequest<ManifestMoveReq, SaveResponse>(
				ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),req,manManService.doUpdateManifest(req,principal.getName()));
	}
}
