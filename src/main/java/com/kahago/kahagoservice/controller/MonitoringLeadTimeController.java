package com.kahago.kahagoservice.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.hibernate.service.spi.Startable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.LeadTimeRequest;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.LeadTimeDetailResponse;
import com.kahago.kahagoservice.model.response.LeadTimeReportResponse;
import com.kahago.kahagoservice.model.response.MonitoringLeadTimeResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.service.MonitoringLeadTimeService;
import com.kahago.kahagoservice.util.ExcelExporter;

import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Slf4j
public class MonitoringLeadTimeController extends Controller{
	@Autowired
	private MonitoringLeadTimeService monitoringLeadTimeService;
	
	@GetMapping("/leadtime/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<MonitoringLeadTimeResponse>> getListLeadTime(@RequestParam(required=false)Integer vendorCode,
																	   @RequestParam(required=false)Integer productCode,
																	   @RequestParam(required=false)String areaId,
																	   @RequestParam(required=false)String userId,
																	   @RequestParam(required=true)String startDate,
																	   @RequestParam(required=true)String endDate){
		log.info("==> GET List Lead Time By :"+vendorCode+","+productCode+","+areaId+","+userId+","+startDate+","+endDate+" <==");
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				monitoringLeadTimeService.getTotalLeadTime(vendorCode, productCode, userId, areaId,startDate,endDate)
				);
	}
	
	@GetMapping("/leadtime/listDetail")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<LeadTimeDetailResponse>> getListLeadTimeDetail(LeadTimeRequest req){
		log.info("==> GET List Lead Time By :"+req.toString()+" <==");
		Page<LeadTimeDetailResponse> lLeadData = monitoringLeadTimeService.getListDetailLeadTime(req, req.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lLeadData),
				lLeadData.getContent()
				);
	}
	
	@GetMapping("/leadtime/report")
	public ResponseEntity<InputStreamResource> getReportLeadTime(@RequestParam(required=false)Integer vendorCode,
																   @RequestParam(required=false)Integer productCode,
																   @RequestParam(required=false)String areaId,
																   @RequestParam(required=false)String userId,
																   @RequestParam(required=true)String startDate,
																   @RequestParam(required=true)String endDate){
		log.info("==> GET List Lead Time By :"+vendorCode+","+productCode+","+areaId+","+userId+","+startDate+","+endDate+" <==");
		List<LeadTimeReportResponse> result = monitoringLeadTimeService.getReportLeadTime(vendorCode, productCode, userId, areaId, startDate, endDate);
		ByteArrayInputStream byteArrayInputStream = new ExcelExporter<>(LeadTimeReportResponse.class, result).exporter();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=Lead-Time-Report.xlsx");
		
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(byteArrayInputStream));
	}
	
	@GetMapping("/leadtime/total")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getTotalLeadTime(@RequestParam(required=false)Integer vendorCode,
													   @RequestParam(required=false)Integer productCode,
													   @RequestParam(required=false)String areaId,
													   @RequestParam(required=false)String userId,
													   @RequestParam(required=false)String startDate,
													   @RequestParam(required=false)String endDate,
													   @RequestParam(required=true)String status,
													   @RequestParam(required=false)String bookingCode){
		log.info("==> GET Total Lead Time By :"+vendorCode+","+productCode+","+areaId+","+userId+","+startDate+","+endDate+" <==");
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				monitoringLeadTimeService.getTotalLeadTimeByProductSw(vendorCode, productCode, userId, areaId, startDate, endDate, status,bookingCode)
				);
	}
	
	@GetMapping("/leadtime/hitmanual")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> getCronLeadTimeManual(@RequestParam(required=false)String bookingCode,
														@RequestParam(required=false)Integer vendorCode){
		log.info("===> Manual HIT Cron Lead Time By BookingCode : "+bookingCode+" Or Vendor : "+vendorCode);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				monitoringLeadTimeService.getStatusTracking(bookingCode, vendorCode)
				);
	}
}
