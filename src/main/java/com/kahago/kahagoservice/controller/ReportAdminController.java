package com.kahago.kahagoservice.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.ReportDailyRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.LeadTimeReportResponse;
import com.kahago.kahagoservice.model.response.ReportResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.ReportService;
import com.kahago.kahagoservice.service.WarehouseService;
import com.kahago.kahagoservice.util.DateTimeUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(description="View Of Reports Admin")
@Slf4j
public class ReportAdminController {
	@Autowired
	private ReportService reportService;
	
	@Autowired
    private WarehouseService warehouseService;
	
	
	@ApiOperation("Laporan Pemesanan")
	@GetMapping("/reportadmin/pemesanan")
	public ResponseEntity<byte[]> getAllPayment(@RequestParam(name="startDate")String startDate,
    													  @RequestParam(name="endDate")String endDate,
    													  @RequestParam(name="format",defaultValue="pdf")String format,
    													  @RequestParam(name="userCategoryId",required=false)Integer userCategory)throws ParseException{
		log.info("==> Report Pemesanan <==");
		LocalDate start = DateTimeUtil.getDateFrom(startDate, "yyyyMMdd");
		LocalDate end = DateTimeUtil.getDateFrom(endDate, "yyyyMMdd");
		Map<String, Object> params = new HashMap<>();
		params.put("dateStart", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		params.put("dateEnd", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		params.put("USERCATEGORY", userCategory);
		return warehouseService.generateFile("laporan-pemesanan", params, format);
	}
	
	@ApiOperation("Laporan Penjualan Global")
	@GetMapping("/reportadmin/penjualanglobal")
	public ResponseEntity<byte[]> getReportGlobal(@RequestParam(name="startDate",required=true)String startDate,
    		                    				@RequestParam(value = "format", defaultValue = "pdf") String format){
		log.info("==> Report Penjualan Global <==");
		Map<String, Object> params = new HashMap<>();
		params.put("dateStart", startDate);
		
		return warehouseService.generateFile("laporan-penjualan-global", params, format);
		
	}
	
	@ApiOperation("Laporan Penjualan Vendor")
	@GetMapping("/reportadmin/penjualanvendor")
	public ResponseEntity<byte[]> getReportGlobalVendor(@RequestParam(name="startDate",required=true)String startDate,
														@RequestParam(value = "format", defaultValue = "pdf") String format){
		log.info("==> Report Penjualan Vendor <==");
		Map<String, Object> params = new HashMap<>();
		params.put("dateStart", startDate);
		
		return warehouseService.generateFile("laporan-penjualan-vendor", params, format);
	}
	
	@ApiOperation("Laporan User Daily")
	@GetMapping("/reportadmin/dailyuser")
	public ResponseEntity<byte[]> getReportDaily(@RequestParam()String startDate,
												 @RequestParam()String endDate,
												 @RequestParam(required=false)String userId,
												 @RequestParam(required=false)String status,
												 @RequestParam(required=false)String vendor,
												 @RequestParam(value = "format", defaultValue = "pdf") String format)throws ParseException{
		log.info("==> Report User Daily <==");
		Boolean all1 = true;
		Boolean all2 = true;
		Boolean all3 = true;
		List<String> arrayList1 = new ArrayList<>();
		List<String> arrayList2 = new ArrayList<>();
		List<String> arrayList3 = new ArrayList<>();
		if(userId != null) {
			all1=false;
			String[] arruser = userId.split(",");
			for(int i=0;i<arruser.length;i++) {
				arrayList1.add(arruser[i]);
			}
		}
		if(vendor != null) {
			all2 = false;
			String[] arrvendor = vendor.split(",");
			for(int i=0;i<arrvendor.length;i++) {
				arrayList2.add(arrvendor[i]);
			}
		}
		if(status != null) {
			all3 = false;
			String[] arrstatus = status.split(",");
			for(int i=0;i<arrstatus.length;i++) {
				arrayList3.add(arrstatus[i]);
			}
		}
		LocalDate start = DateTimeUtil.getDateFrom(startDate, "yyyyMMdd");
		LocalDate end = DateTimeUtil.getDateFrom(endDate, "yyyyMMdd");
		Map<String, Object> params = new HashMap<>();
		params.put("dateStart", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		params.put("dateEnd", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		params.put("array1", arrayList1);
		params.put("array2", arrayList2);
		params.put("array3", arrayList3);
		params.put("all1", all1);
		params.put("all2", all2);
		params.put("all3", all3);
		
		return warehouseService.generateFile("daily-users-report", params, format);
		
	}
}
