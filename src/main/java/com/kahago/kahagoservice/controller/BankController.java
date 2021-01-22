package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.BankRes;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.BankService;
import io.swagger.annotations.ApiImplicitParam;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class BankController extends Controller {

    @Autowired
    private BankService bankService;
    
    private static final Logger log = LoggerFactory.getLogger(BankController.class);

//    @GetMapping("/bank")
//    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
//    public Response<List<BankRes>> find(Pageable pageable) {
//        Page<BankRes> banks = bankService.findBanks(pageable);
//
//        return new Response<>(
//                ResponseStatus.OK.value(),
//                ResponseStatus.OK.getReasonPhrase(),
//                extraPaging(banks),
//                banks.getContent()
//        );
//    }
    
    @GetMapping("/bank")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<BankRes>> find(HttpServletRequest req) {
    	log.info("==>List of bank<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        List<BankRes> banks = bankService.findAll();
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                banks
                
        );
    }
    @GetMapping(value = "/bank/logo/{kode}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public @ResponseBody byte[] getImage(@PathVariable String kode) throws IOException {
		String pathpic = bankService.getPathImage(kode.substring(0, 3));
	    InputStream in = new FileInputStream(new File(pathpic));
	    return IOUtils.toByteArray(in);
	}
}
