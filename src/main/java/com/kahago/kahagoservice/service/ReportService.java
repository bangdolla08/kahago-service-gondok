package com.kahago.kahagoservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.TLeadTimeEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DetailStatus;
import com.kahago.kahagoservice.model.request.ReportDailyRequest;
import com.kahago.kahagoservice.model.request.UserDetail;
import com.kahago.kahagoservice.model.request.Vendor;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.LeadTimeReportResponse;
import com.kahago.kahagoservice.model.response.ReportResponse;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.TLeadTimeRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author bangd ON 03/12/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class ReportService {
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private TPaymentRepo tPaymentRepo;
    @Autowired
    private MSwitcherRepo mSwitcherRepo;
    @Autowired
    private TLeadTimeRepo tLeadTimeRepo;
    
    private final static Logger log = LoggerFactory.getLogger(ReportService.class);
    
    public List<BookDataResponse> getAllPayment(String start,String end){
    	log.info("===>Request Report<===");
    	List<TPaymentEntity> lPayment = new ArrayList<>();
    	try {
    		LocalDate startDate = DateTimeUtil.getDateFrom(start, "ddMMyyyy");
    		LocalDate endDate = DateTimeUtil.getDateFrom(end, "ddMMyyyy");
    		lPayment = tPaymentRepo.getPaymentBytrxDate(startDate, endDate);
    	}catch (Exception e) {
			// TODO: handle exception
    		log.error(e.getMessage());
    		e.printStackTrace();
    		
		}
    	return lPayment.stream().map(this::getPaymentDto).collect(Collectors.toList());
    }
    
    private BookDataResponse getPaymentDto(TPaymentEntity entity) {
    	return paymentService.toBookDataResponse(entity);
    }
    
    public List<ReportResponse> getReportGlobal(String start,String end){
    	log.info("===>Laporan Penjualan Global<===");
    	List<ReportResponse> lresponse = new ArrayList<>();
    	try {
    		LocalDate startDate = DateTimeUtil.getDateFrom(start, "ddMMyyyy");
    		LocalDate endDate = DateTimeUtil.getDateFrom(end, "ddMMyyyy");
    		int size = endDate.compareTo(startDate);
    		for(int x=0;x<=size;x++) {
    			LocalDate trxDate = startDate.plusDays(x);
    			Long totaltrx = tPaymentRepo.countStatus(trxDate);
    			Long totalweight = tPaymentRepo.totalWeightByDay(trxDate,null);
    			ReportResponse rpt = ReportResponse.builder()
    					.trxDate(trxDate)
    					.totalTrx(totaltrx)
    					.totalWeight(totalweight)
    					.build();
    			lresponse.add(rpt);
    		}
    	}catch (Exception e) {
			// TODO: handle exception
    		log.error(e.getMessage());
    		e.printStackTrace();
		}
    	return lresponse;
    }
    
    public List<ReportResponse> getAllReportVendor(String start,String end){
    	log.info("===>Laporan Penjualan Global Per Vendor<===");
    	List<ReportResponse> lresponse = new ArrayList<>();
    	List<MSwitcherEntity> lvendor = mSwitcherRepo.findAll();
    	try {
    		LocalDate startDate = DateTimeUtil.getDateFrom(start, "ddMMyyyy");
    		LocalDate endDate = DateTimeUtil.getDateFrom(end, "ddMMyyyy");
    		int size = endDate.compareTo(startDate);
    		for(MSwitcherEntity sw : lvendor) {
    			for(int x=0;x<=size;x++) {
        			LocalDate trxDate = startDate.plusDays(x);
        			Long totaltrx = tPaymentRepo.countStatusAndSwitcherByDay(trxDate, sw);
        			Long totalweight = tPaymentRepo.totalWeightByDay(trxDate,sw);
        			ReportResponse rpt = ReportResponse.builder()
        					.trxDate(trxDate)
        					.totalTrx(totaltrx==null?0:totaltrx)
        					.totalWeight(totalweight==null?0:totalweight)
        					.vendorCode(sw.getSwitcherCode())
        					.vendorName(sw.getName())
        					.build();
        			lresponse.add(rpt);
        		}
    		}
    		
    	}catch (Exception e) {
			// TODO: handle exception
    		log.error(e.getMessage());
    		e.printStackTrace();
		}
    	return lresponse;
    }
    
    public List<BookDataResponse> getReportDeally(ReportDailyRequest request){
    	List<TPaymentEntity> lPayment = new ArrayList<>();
    	List<Integer> status = null;
    	List<String> userId = null;
    	List<Integer> vendor = null;
    	if(request.getStatus()!=null) {
    		status = new ArrayList<>();
    		for(DetailStatus st:request.getStatus()) {
        		status.add(st.getStatus());
        	}
    	}
    	if(request.getUserId()!=null) {
    		userId = new ArrayList<>();
    		for(UserDetail ud:request.getUserId()) {
        		userId.add(ud.getUserId());
        	}
    	}
    	if(request.getVendor()!=null) {
    		vendor=new ArrayList<>();
    		for(Vendor ve:request.getVendor()) {
        		vendor.add(ve.getSwitcherCode());
        	}
    	}
    	
    	try {
    		LocalDate startDate = DateTimeUtil.getDateFrom(request.getStartDate(), "ddMMyyyy");
    		LocalDate endDate = DateTimeUtil.getDateFrom(request.getEndDate(), "ddMMyyyy");
    		lPayment = tPaymentRepo.getPaymentByVendor(startDate, endDate, status, userId, vendor);
    	}catch (Exception e) {
			// TODO: handle exception
    		log.error(e.getMessage());
    		e.printStackTrace();
		}
    	
    	return lPayment.stream().map(this::getPaymentDto).collect(Collectors.toList());
    }
}
