package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.GoodsRes;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.GoodsService;
import io.swagger.annotations.ApiImplicitParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class GoodsController {

    @Autowired
    private GoodsService service;
    
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @GetMapping("/goods")
    public Response<List<GoodsRes>> getAll(HttpServletRequest req) {
    	log.info("==>Goods<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                service.findAllGoods()
        );
    }
    @GetMapping("/goods/{productcode}")
    public Response<List<GoodsRes>> getAll(@PathVariable String productcode,HttpServletRequest req) {
    	log.info("==>goods by product code<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                service.findAllGoods(productcode)
        );
    }
}
