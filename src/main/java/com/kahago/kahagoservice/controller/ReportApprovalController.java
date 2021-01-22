package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.model.response.ApprovalReport;
import com.kahago.kahagoservice.service.ApprovalTopupService;
import com.kahago.kahagoservice.util.ExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class ReportApprovalController {

    @Autowired
    private ApprovalTopupService approvalTopupService;

    @GetMapping("/report/approval/{type}")
    public ResponseEntity<InputStreamResource> reportTopupCredit(
            @PathVariable("type") String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {

        String depositType;
        if (type.equals("credit")) {
            depositType = DepositTypeEnum.CREDIT.getValue();
        } else {
            depositType = DepositTypeEnum.DEPOSIT.getValue();
        }

        List<ApprovalReport> entities = approvalTopupService.reportApprovalTopUp(depositType, status, startDate, endDate);
        ByteArrayInputStream byteArrayInputStream = new ExcelExporter<>(ApprovalReport.class, entities).exporter();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=approval-"+type+".xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(byteArrayInputStream));
    }
}
