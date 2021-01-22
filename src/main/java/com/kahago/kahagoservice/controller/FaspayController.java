package com.kahago.kahagoservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.client.model.request.NotificationFaspay;
import com.kahago.kahagoservice.client.model.response.ResponseNotifFaspay;
import com.kahago.kahagoservice.component.FaspayComponent;
import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BaseController
@ResponseBody
public class FaspayController extends Controller{
	@Autowired
	private FaspayComponent faspay;
	@GetMapping("/faspay/payment")
	public ResponseGlobal getStatusCallback(@RequestParam("merchant_id") String merchantid,
			@RequestParam("bill_no") String billNo,
			@RequestParam(value="Request",required=false) String request,
			@RequestParam(value="trx_id",required=false) String trxid,
			@RequestParam(value="bill_ref",required=false) String billreff,
			@RequestParam(value="bill_total",required=false) String billtotal,
			@RequestParam(value="payment_reff",required=false) String paymentReff,
			@RequestParam(value="Payment_date",required=false) String paymentDate,
			@RequestParam(value="bank_user_name",required=false) String bankUsername,
			@RequestParam(value="status",required=false) String status,
			@RequestParam(value="signature",required=false) String signature,
			HttpServletRequest req) {
		log.info("===> Callback Faspay <====");
		log.info("Parameter => "+ req.getQueryString());
		return faspay.getValidasi(billNo, status);
	}
	
	 @ApiOperation(value = "Test Push notif faspay")
    @ApiResponses(value={@ApiResponse(code= 200,message="Faspay Simulator")})
	@PostMapping(value="/faspay/notification",consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseNotifFaspay doUpdateNotif(@RequestBody NotificationFaspay notif) {
		log.info("==> Notification Faspay <===");
		log.info("Request: "+Common.json2String(notif));
		
		return faspay.doUpdate(notif);
	}
}
