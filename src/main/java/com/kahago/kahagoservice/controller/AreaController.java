package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PriceListRequest;
import com.kahago.kahagoservice.model.request.TotalTrxRequest;
import com.kahago.kahagoservice.model.response.KecamatanResponse;
import com.kahago.kahagoservice.model.response.KelurahanResponse;
import com.kahago.kahagoservice.model.response.OriginResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.model.response.VendorAreaDetail;
import com.kahago.kahagoservice.service.AreaService;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.validation.UserMustExist;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;

import java.security.Principal;
import java.util.List;

/**
 * @author bangd ON 17/11/2019
 *
 */
@BaseController
@ResponseBody
@Api(value = "Area Management System", description = "Operations pertaining to area of KahaGo coverage")
public class AreaController extends Controller {
    @Autowired
    private AreaService areaService;
    
    private static final Logger log = LoggerFactory.getLogger(AreaController.class);
    
    @ApiOperation(value = "View a list of origin")
    @GetMapping("/area/origin")
    public Response<List<OriginResponse>> getOrigin(HttpServletRequest req){
    	log.info("==>List of origin<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getOrigin());
    }
    @ApiOperation(value = "View a list of origin")
    @GetMapping("/area/origins/{office_code}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<OriginResponse>> getOrigin(@PathVariable String office_code,HttpServletRequest req){
        log.info("==>List of origin<===");
        log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getOrigin(office_code));
    }

    @GetMapping("/area/districts")
    public Response<List<KecamatanResponse>> getDistricts(HttpServletRequest req){
    	log.info("==>List of district<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getKecamatan());
    }

    @GetMapping("/area/postalcode")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<KelurahanResponse>> getPostalCode(@RequestParam("id_kecamatan") Integer idKecamatan,HttpServletRequest req){
    	log.info("==>List of kelurahan<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getKelurahan(idKecamatan));
    }
    @GetMapping("/area/provinsi")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<KelurahanResponse>> getProvice(HttpServletRequest req){
    	log.info("==>List of provinsi<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getProvice());
    }

    @GetMapping("/area/kota/{proviceId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<KelurahanResponse>> getCity(@PathVariable Integer proviceId,HttpServletRequest req){
    	log.info("==>List of kota<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getCity(proviceId));
    }

    @GetMapping("/area/kecamatan/{cityId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<KelurahanResponse>> getKecamatan(@PathVariable Integer cityId,HttpServletRequest req){
    	log.info("==>List of kecamatan<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getKecamatan(cityId));
    }

    @GetMapping("/area/kelurahan/postalcode/{postalCode}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<KelurahanResponse>> getKelurahanByPostalCode(@PathVariable String postalCode,HttpServletRequest req){
    	log.info("==>List of postal code<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                areaService.getKelurahanByPostalCode(postalCode));
    }
    
    @ApiOperation(value="Get Total Late Sync")
	@GetMapping("/area/totallatesync")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getTotalLateSync(HttpServletRequest req){
		log.info("===>Total Transaksi<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				areaService.getTotalLateSync()
				);
	}

    @ApiOperation(value="Get Total Selisih")
	@GetMapping("/area/totalselisih")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getTotalTrx(HttpServletRequest req){
		log.info("===>Total Transaksi<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				areaService.getTotalBySelisih()
				);
	}
    
    @ApiOperation(value="Get Total Exception")
	@PostMapping("/area/totalcronprice")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getTotalCronPrice(@RequestBody TotalTrxRequest req,Principal principal){
		log.info("===>Total Transaksi<===");
    	log.info("Request ==> "+principal.getName());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				areaService.getTotalVendorAreaBystatus(req)
				);
	}
    
    @ApiOperation(value="List Area Id Harga Selisih")
	@GetMapping("/area/diff/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<VendorAreaDetail>> getListPriceDiff(PriceListRequest req,Principal principal){
		log.info("===>Total Transaksi<===");
    	log.info("Request ==> "+Common.json2String(req));
    	Page<VendorAreaDetail> resp = areaService.getDataVendorPage(req);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(resp),
				resp.getContent()
				);
	}
    
    @ApiOperation(value="Edit isCheck is true")
    @PostMapping("/area/diff/isCheck")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> editIsCheck(@RequestBody VendorAreaDetail request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			areaService.updateFlagArea(request)
    			);
    }
}
