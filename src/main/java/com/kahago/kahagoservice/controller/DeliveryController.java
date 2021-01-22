package com.kahago.kahagoservice.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.DeliveryReq;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.response.DeliveryResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.DeliveryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Delivery",description="Operating abount list delivery")
public class DeliveryController extends Controller{
	@Autowired
	private DeliveryService deliveryService;
	
	private static final int limit = 10;
	private static final Logger log = LoggerFactory.getLogger(DeliveryController.class);
	@ApiOperation(value="List of Delivery payment")
	@GetMapping("/delivery")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<DeliveryResponse>> getAssignPickup(DeliveryReq request,HttpServletRequest req) {
		log.info("==>List of book<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		List<DeliveryResponse> ldel = new ArrayList<DeliveryResponse>();
		PageHeaderRequest page = new PageHeaderRequest();
		int end = 10;
		if(request.getPage() != null) {
			page.setPageNumber(request.getPage() - 1);
			page.setPageSize(5);
		}
		ldel = deliveryService.getPayment(request, page.getPageRequest());
		
		if(ldel.size() < 10 ) {
			end = ldel.size();
		}
		if(request.getPage() == null) {
			end = ldel.size();
		}
		Page<DeliveryResponse> pageDelivery = new PageImpl<>(ldel.subList(0, end),page.getPageRequest(), ldel.size());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				pageDelivery.getContent()
				);
	}
	
}
