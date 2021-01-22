package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.dto.MutasiDompetDto;
import com.kahago.kahagoservice.model.request.HistoryBookRequest;
import com.kahago.kahagoservice.model.request.MutasiReq;
import com.kahago.kahagoservice.model.request.ReferenceRequest;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.service.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * @author bangd ON 25/11/2019
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Api(value = "Report System Controller")
public class ReportController extends Controller {
    @Autowired
    private TopupService mutasiService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReviewBookService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private PickupService pickupService;
    @Autowired
    private ResiService resiService;

    @Autowired
    private DataSource dataSource;
    
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    @GetMapping("/report/mutasi")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<MutasiReq, List<MutasiDompetDto>> getMutasi(MutasiReq mutasiReq,HttpServletRequest req){
    	log.info("==> Mutasi <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        ResponseWithRequest<MutasiReq,List<MutasiDompetDto>> response=new ResponseWithRequest<>();
        Page<MutasiDompetDto> mutasiDtos=mutasiService.getMutasiDto(mutasiReq.getPageable(),mutasiReq);
        response.setRequest(mutasiReq);
        response.setData(mutasiDtos.getContent());
        response.setPage(extraPaging(mutasiDtos));
        return response;
    }

    @GetMapping("/report/historytrx")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<HistoryBookRequest, List<BookDataResponse>> getHistory(HistoryBookRequest historyBook,HttpServletRequest req) throws ParseException {
    	log.info("==> History trx <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<HistoryBookRequest,List<BookDataResponse>> response=new ResponseWithRequest<>();
        Page<BookDataResponse> mutasiDtos=paymentService.historyBook(historyBook);
        response.setRequest(historyBook);
        response.setData(mutasiDtos.getContent());
        response.setPage(extraPaging(mutasiDtos));
        return response;
    }
    
    @ApiOperation(value="Detail transaksi")
	@GetMapping("/report/trx/detail")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<DetailTrxResponse> getDetaiTrx(@RequestParam("bookingCode") String bookingCode,
												   @RequestParam("userId") String userId,
												   @RequestParam(value="qrcode",required=false,defaultValue="-") String qrcode,
												   @RequestParam(value="isBooking",required=false) Boolean isBooking,
												   HttpServletRequest req){
    	log.info("==> Detail trx <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	if(isBooking==null) {
    		isBooking=true;
    	}
    	return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				reviewService.getDetailTrx(bookingCode, userId,qrcode,isBooking)
				);
	}

    @GetMapping("/report/feereference")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<ReferenceRequest, ReferenceReport> getFeeReferance(ReferenceRequest referenceRequest,HttpServletRequest req) throws ParseException {
    	log.info("==> Freereference <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<ReferenceRequest,ReferenceReport> response=new ResponseWithRequest<>();
        ReferenceReport reference = userService.getReference(referenceRequest,"");
        response.setRequest(referenceRequest);
        response.setData(reference);
        response.setPage(extraPaging(reference.getDetail()));
        return response;
    }
    
    @GetMapping("/report/feereference/{userId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<ReferenceRequest, ReferenceReport> getFeeReferance(@PathVariable String userId, ReferenceRequest referenceRequest,HttpServletRequest req) throws ParseException {
    	log.info("==> feereference <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<ReferenceRequest,ReferenceReport> response=new ResponseWithRequest<>();
        ReferenceReport userReverance=userService.getReference(referenceRequest,userId);
        response.setRequest(referenceRequest);
        response.setData(userReverance);
        response.setPage(extraPaging(userReverance.getDetail()));
        return response;
    }

    @GetMapping("/report/manifest/pickup")
    public void showResiKahago(@RequestParam("id") String id,
                               @RequestParam(value = "format", defaultValue = "pdf") String format,
                               HttpServletResponse response) throws JRException, IOException, SQLException {

        Map params = pickupService.findByManifest(id);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/manifest-pickup-report.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());

        if(format.equals("pdf")) {
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        } else if (format.equals("xls")) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            ByteArrayOutputStream xlsBytes = new ByteArrayOutputStream();
            SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
            reportConfig.setSheetNames(new String[] { "manifest" });
            reportConfig.setRemoveEmptySpaceBetweenRows(true);
            exporter.setConfiguration(reportConfig);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsBytes));
            exporter.exportReport();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=manifest-pickup.xls");
            response.getOutputStream().write(xlsBytes.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            response.flushBuffer();
        }
    }

    @GetMapping("/report/manifest/pos")
    public void showResiPos(@RequestParam("manifest") String manifest,
                            HttpServletResponse response) throws JRException, IOException, SQLException {
        Map params = resiService.mappingManifestPos(manifest);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/manifest-pos.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
    
    @GetMapping("/report/permohonan")
    public void showPermohonan(@RequestParam("nopermohonan") String nopermohonan,
                            HttpServletResponse response) throws JRException, IOException, SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("ids", nopermohonan);
        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/permohonan-vendor-report.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}
