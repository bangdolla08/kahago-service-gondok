package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.service.ResiService;
import com.kahago.kahagoservice.service.WarehouseService;

import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.ResourceBundle.Control;

/**
 * @author Hendro yuwono
 */
@BaseController
public class ResiController extends Controller{

    @Autowired
    private ResiService resiService;
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/resi/kahago")
    public void showResiKahago(@RequestParam("bookingcode") String bookingCode,
                             @RequestParam("userid") String userId, 
                             @RequestParam(value = "is_boc", required = false,defaultValue = "0") String isBoc, HttpServletResponse response) throws JRException, IOException {

        Map params = resiService.mappingResiKahago(bookingCode, userId,isBoc);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/resi_thermal.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    @GetMapping("/resi/tiki")
    public void showResiTiki(@RequestParam("bookingcode") String bookingCode,
                             @RequestParam("userid") String userId, HttpServletResponse response) throws JRException, IOException {
        Map params = resiService.mappingResiTiki(bookingCode, userId);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/tiki.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    @GetMapping("/resi/wahana")
    public void showResiWahana(@RequestParam("bookingcode") String bookingCode,
                             @RequestParam("userid") String userId, HttpServletResponse response) throws JRException, IOException {
        Map params = resiService.mappingResiWahana(bookingCode, userId);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/resiwahana.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    @GetMapping("/resi/pos")
    public void showResiPos(@RequestParam("bookingcode") String bookingCode,
                            @RequestParam("userid") String userId,
                            @RequestParam("officerid") String officerid,
                            HttpServletResponse response) throws JRException, IOException {
        Map params = resiService.mappingResiPos(bookingCode, userId, officerid);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/posnew.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
    
    @GetMapping("/warehouse/print")
    @ApiOperation(value = "Print Document outgoing")
    public ResponseEntity<byte[]> printOut(@RequestParam(name="tisCable")String tisCable,
    										@RequestParam(name="userId")String userId){
    	return warehouseService.getReportOutgoing(tisCable,userId);
    }
    
    @GetMapping("/resi/indah")
    public void showResiIndah(@RequestParam("bookingcode") String bookingCode,
                             @RequestParam("userid") String userId, HttpServletResponse response) throws JRException, IOException {
        Map params = resiService.mappingResiWahana(bookingCode, userId);

        InputStream stream = ResiController.class.getClassLoader().getResourceAsStream("reports/resiindah.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}
