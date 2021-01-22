package com.kahago.kahagoservice.controller;

import java.util.List;

import com.kahago.kahagoservice.model.projection.IncomingOfGood;
import com.kahago.kahagoservice.model.response.Result;
import com.kahago.kahagoservice.model.validate.IncomingRequest;
import com.kahago.kahagoservice.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.RequestBook;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.GlobalListService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BaseController
@ResponseBody
@Api(value = "Global List Controller", description = "View List")
public class GlobalListController extends Controller{

	@Autowired
	private GlobalListService globalListService;

	@Autowired
	private WarehouseService warehouseService;
	
	@ApiOperation(value="List of Global List")
	@PostMapping("/global/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<BookDataResponse>> getGlobalList(@RequestBody RequestBook request){
		log.info("==> Booking List "+request.getModul()+" <==");
		
		Page<BookDataResponse> pagelist = globalListService.getBookingList(request);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				this.extraPaging(pagelist),
				pagelist.getContent()
				);
	}

	@GetMapping("/warehouse/incoming")
	public Result listOfIncoming(@RequestParam("office_code") String officeCode, Pageable pageable) {
		Page<IncomingOfGood> incoming = warehouseService.incoming(officeCode, pageable);
		return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), pagination(incoming), incoming.getContent());
	}

	@GetMapping("/warehouse/incoming/{qrCode}")
	public Result fetchByQrCode(@PathVariable String qrCode, @RequestParam("office_code") String officeCode) {

		IncomingRequest request = IncomingRequest.builder()
				.officeCode(officeCode)
				.qrCode(qrCode)
				.build();

		return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), warehouseService.findByQrCode(request));
	}

	@PutMapping("/warehouse/incoming/{qrCode}")
	public Result processByQrCode(@PathVariable String qrCode, @RequestParam("office_code") String officeCode) {
		IncomingRequest request = IncomingRequest.builder()
				.officeCode(officeCode)
				.qrCode(qrCode)
				.build();

		warehouseService.updatePiecesStatus(request);
		return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());

	}
}
