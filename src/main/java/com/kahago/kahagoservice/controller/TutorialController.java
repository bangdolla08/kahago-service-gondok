package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.EditTutorialRequest;
import com.kahago.kahagoservice.model.request.NewTutorialRequest;
import com.kahago.kahagoservice.model.request.PromoRequest;
import com.kahago.kahagoservice.model.response.PromoRes;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.TutorialService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;
    
    private static final Logger log = LoggerFactory.getLogger(TutorialController.class);

    @GetMapping("/tutorial")
    public Response<List<PromoRes>> findTutorial(HttpServletRequest req) {
    	log.info("==> Tutorial <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), tutorialService.findPromo());
    }


    @GetMapping("/tutorial/{idPromo}")
    public Response<PromoRes> findTutorialId(@PathVariable Integer idPromo,HttpServletRequest req) {
    	log.info("==> Promo <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), tutorialService.findById(idPromo));
    }
    
    @GetMapping("/tutorial/list")
    public Response<List<PromoRes>> getAll(HttpServletRequest req){
    	log.info("==> List Tutorial <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			tutorialService.getAll()
    			);
    }
    
    @ApiOperation("tutorial for web")
    @GetMapping("/tutorial/front")
    public Response<List<PromoRes>> getAllTutorial(HttpServletRequest req){
    	log.info("==> List Tutorial Web<==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			tutorialService.getAllTutorial()
    			);
    }
    
    @PostMapping("/promo/save")
    @ApiOperation("Save New Promo")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> savePromo(@RequestBody PromoRequest request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			tutorialService.savePromo(request)
    			);
    }
    
    @PostMapping("/promo/edit")
    @ApiOperation("Edit Promo")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveEdit(@RequestBody PromoRequest request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			tutorialService.saveEdit(request)
    			);
    }
    
    @DeleteMapping("/promo/delete/{id}")
    @ApiOperation("Delete Promo")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deletePromo(@PathVariable("id") Integer id){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			tutorialService.deletePromo(id)
    			);
    }
}
