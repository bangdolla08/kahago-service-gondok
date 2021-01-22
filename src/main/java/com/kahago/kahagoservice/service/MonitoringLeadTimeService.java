package com.kahago.kahagoservice.service;

import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.client.FeignService;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TLeadTimeEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.LeadTimeStatusEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.LeadTimeRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.LeadTimeDetailResponse;
import com.kahago.kahagoservice.model.response.LeadTimeDtlResponse;
import com.kahago.kahagoservice.model.response.LeadTimeProductResponse;
import com.kahago.kahagoservice.model.response.LeadTimeReportResponse;
import com.kahago.kahagoservice.model.response.MonitoringLeadTimeResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.repository.TLeadTimeRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

import feign.FeignException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Service
@Slf4j
public class MonitoringLeadTimeService {
	@Autowired
	private TLeadTimeRepo tLeadTimeRepo;
	
	@Autowired
	private MSwitcherRepo mSwitcherRepo;
	
	@Autowired
	private MProductSwitcherRepo mProductSwitcherRepo;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private TAreaRepo tAreaRepo;
	
	@Autowired
	private MAreaRepo mAreaRepo;
	@Autowired
	private MOfficeRepo officeRepo;
	@Autowired
	private FeignService feignService;
	
	@Value("${url.cron.lead.time}")
	private String urlcron;
	
	@Value("${cron.start}")
	private String dateStart;
	
	private static final List<Integer> VENDOR_EXCEPT = new ArrayList<>(Arrays.asList(300,304));
	
	public List<MonitoringLeadTimeResponse> getTotalLeadTime(Integer vendorCode,Integer productCode,String userId, String areaId,String start,String end){
		List<MonitoringLeadTimeResponse> result = new ArrayList<>();
		LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		if(vendorCode == null && productCode == null) {
			List<MSwitcherEntity> lvendor = mSwitcherRepo.findAllBySwitcherCodeNotIn(VENDOR_EXCEPT);
			for(MSwitcherEntity vendor : lvendor) {
				MonitoringLeadTimeResponse ltResponse = new MonitoringLeadTimeResponse();
				ltResponse.setSwitcherCode(vendor.getSwitcherCode());
				ltResponse.setVendorName(vendor.getName());
				ltResponse.setSwitcherImage(PREFIX_PATH_IMAGE_VENDOR + vendor.getImg().substring(vendor.getImg().lastIndexOf("/") + 1));
				List<Object[]> listLedTime = tLeadTimeRepo.getTotalLeadTimeByProduct(productCode, areaId, userId, vendor.getSwitcherCode(),startDate,endDate);
				List<LeadTimeProductResponse> lproduct = new ArrayList<LeadTimeProductResponse>();
				List<Integer> lproductCode = new ArrayList<>();
				for(Object[] leadTime : listLedTime) {
					Integer product = Integer.valueOf(leadTime[2].toString());
					if(lproductCode.size() == 0) {
						LeadTimeProductResponse leadTimeProduct = getLeadTimeProduct(product, listLedTime);
						lproduct.add(leadTimeProduct);
					}else {
						if(!lproductCode.contains(product)) {
							LeadTimeProductResponse leadTimeProduct = getLeadTimeProduct(product, listLedTime);
							lproduct.add(leadTimeProduct);
						}
					}
					lproductCode.add(product);
				}
				ltResponse.setDetailProduct(lproduct);
				result.add(ltResponse);
			}
		}else {
			MonitoringLeadTimeResponse ltResponse = new MonitoringLeadTimeResponse();
			if(productCode != null) {
				MProductSwitcherEntity prEntity = mProductSwitcherRepo.findByProductSwCode(productCode.longValue());
				ltResponse.setSwitcherCode(prEntity.getSwitcherEntity().getSwitcherCode());
				ltResponse.setVendorName(prEntity.getSwitcherEntity().getName());
			}
			if(vendorCode != null) {
				MSwitcherEntity vendor = mSwitcherRepo.findById(vendorCode).orElseThrow(()->new NotFoundException("Vendor Tidak Ditemukan !"));
				ltResponse.setSwitcherCode(vendor.getSwitcherCode());
				ltResponse.setVendorName(vendor.getName());
			}			
			List<Object[]> listLedTime = tLeadTimeRepo.getTotalLeadTimeByProduct(productCode, areaId, userId, vendorCode, startDate, endDate);
			List<LeadTimeProductResponse> lproduct = new ArrayList<LeadTimeProductResponse>();
			List<Integer> lproductCode = new ArrayList<>();
			for(Object[] leadTime : listLedTime) {
				Integer product = Integer.valueOf(leadTime[2].toString());
				if(lproductCode.size() == 0) {
					LeadTimeProductResponse leadTimeProduct = getLeadTimeProduct(product, listLedTime);
					lproduct.add(leadTimeProduct);
				}else {
					if(!lproductCode.contains(product)) {
						LeadTimeProductResponse leadTimeProduct = getLeadTimeProduct(product, listLedTime);
						lproduct.add(leadTimeProduct);
					}
				}
				lproductCode.add(product);
			}
			ltResponse.setDetailProduct(lproduct);
			result.add(ltResponse);
		}
		setTotalByVendor(result);
		return result;
	}
	
	private LeadTimeProductResponse getLeadTimeProduct(Integer productCode,List<Object[]> leadTime) {
		LeadTimeProductResponse response = new LeadTimeProductResponse();
		List<LeadTimeDtlResponse> lstatusDtl = new ArrayList<>();
		MProductSwitcherEntity pEntity = mProductSwitcherRepo.findByProductSwCode(productCode.longValue());
		response.setProductCode(pEntity.getProductSwCode().intValue());
		response.setProductName(pEntity.getName());
		for(Object[] lt:leadTime) {
			Integer product = Integer.valueOf(lt[2].toString());
			String status = (String) lt[1];
			Integer total = Integer.valueOf(lt[0].toString());
			if(productCode.equals(product)) {
				LeadTimeDtlResponse statusDtl = new LeadTimeDtlResponse();
				statusDtl.setStatus(status);
				statusDtl.setTotal(total);
				lstatusDtl.add(statusDtl);
			}
		}
		/*for(String status : LeadTimeStatusEnum.getListStatusString()) {
			Boolean flag = false;
			for(LeadTimeDtlResponse dtl : lstatusDtl) {
				if(dtl.getStatus().equals(status))
					flag = true;
			}
			if(!flag) {
				LeadTimeDtlResponse statusDtl = new LeadTimeDtlResponse();
				statusDtl.setStatus(status);
				statusDtl.setTotal(0);
				lstatusDtl.add(statusDtl);
			}
		}*/
		response.setDetailStatus(lstatusDtl);
		return response;
	}
	
	private void setTotalByVendor(List<MonitoringLeadTimeResponse> result) {
		for(MonitoringLeadTimeResponse leadResponse : result) {
			List<LeadTimeDtlResponse> lstatusDtl = new ArrayList<>();
			for(String status : LeadTimeStatusEnum.getListStatusString()) {
				LeadTimeDtlResponse statusDtl = new LeadTimeDtlResponse();
				statusDtl.setStatus(status);
				statusDtl.setTotal(0);
				for(LeadTimeProductResponse productRes : leadResponse.getDetailProduct()) {
						for(LeadTimeDtlResponse dtl:productRes.getDetailStatus()) {
							if(dtl.getStatus().equals(status)) {
								statusDtl.setStatus(status);
								statusDtl.setTotal(statusDtl.getTotal()+dtl.getTotal());
							}
						}
					}
				lstatusDtl.add(statusDtl);
			}
			leadResponse.setTotalVendor(lstatusDtl);
		}
	}
	
	public Page<BookDataResponse> getListBookingByLeadTimeStatus(LeadTimeRequest req,Pageable pageable){
		LocalDate startDate = LocalDate.parse(req.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate endDate = LocalDate.parse(req.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		List<BookDataResponse> result = new ArrayList<>();
		Page<TPaymentEntity> lPayment = paymentService.getListBookingByLeadTimeStatus(req.getProductCode(), req.getVendorCode(), startDate, endDate, req.getStatus().toUpperCase(),req.getUserId(),req.getAreaId(),pageable);
		for(TPaymentEntity entity :lPayment.getContent()) {
			BookDataResponse bookRes = paymentService.toBookDataResponse(entity);
			result.add(bookRes);
		}
		
		return new PageImpl<>(result, pageable, lPayment.getTotalElements());
	}
	
	public List<LeadTimeReportResponse> getReportLeadTime(Integer vendorCode,Integer productCode,String userId, String areaId,String start,String end){
		LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		List<LeadTimeReportResponse> result = new ArrayList<>();
		List<Object[]> lentity = tLeadTimeRepo.getDetailLeadTime(productCode, areaId, userId, vendorCode, startDate, endDate);
		Integer no = 0;
		for(Object[] entity : lentity) {
			String estimasi = "hari";
			MAreaEntity mAreaEntity = mAreaRepo.findByAreaName(entity[7].toString());
			//MPostalCodeEntity postalCode = mPostalCodeRepo.findByIdPostalCode(Integer.valueOf(entity[13].toString()));
			//MProductSwitcherEntity product = mProductSwitcherRepo.findByProductSwCode(Long.valueOf(entity[12].toString()));
			List<TAreaEntity> area = tAreaRepo.findTopByProductSwCodeProductSwCodeAndAreaIdIdPostalCodeAndAreaOriginIdOrderByTarifAsc(Long.valueOf(entity[12].toString()), Integer.valueOf(entity[13].toString()), mAreaEntity.getAreaId());
			Integer total=0;
			Integer selisih=0;
			LocalDateTime leave= LocalDateTime.now();
			LocalDateTime arrive = LocalDateTime.now();
			if(entity[9] != null && entity[10] != null) {
				leave = LocalDateTime.parse(entity[9].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
				arrive = LocalDateTime.parse(entity[10].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
				total = arrive.compareTo(leave);
			}
			if(area.size() > 0) {
				if(area.get(0).getStartDay() != null && area.get(0).getEndDay() != null) {
					estimasi = area.get(0).getStartDay()+"-"+area.get(0).getEndDay()+" hari";
					selisih = area.get(0).getEndDay() - total;
				}
			}
			Integer seq = no++;
			LeadTimeReportResponse resp = LeadTimeReportResponse.builder()
											.no(seq.toString())
											.tanggalBooking(entity[0].toString())
											.cabang(entity[1].toString())
											.userId(entity[2].toString())
											.kodeBooking(entity[3].toString())
											.vendor(entity[4].toString())
											.produk(entity[5].toString())
											.resi(entity[6].toString())
											.origin(entity[7].toString())
											.destinasi(entity[8].toString())
											.estimasi(estimasi)
											.tanggalKirim(entity[9]==null?"":entity[9].toString())
											.tanggalSampai(entity[10]==null?"":entity[10].toString())
											.totalEstimasi(total.toString())
											.selisih(selisih.toString())
											.keterangan(entity[11].toString())
											.build();
			result.add(resp);
		}
		return result;
	}
	
	public TotalTrxResponse getTotalLeadTimeByProductSw(Integer vendorCode,Integer productCode,String userId, String areaId,String start,String end,String status,String bookingCode) {
		log.info("===> GET Total Lead Time <===");
		LocalDate startDate = LocalDate.parse(dateStart, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate endDate = LocalDate.now();
		if(start != null && end != null) {
			startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		
		Integer totatTrx = tLeadTimeRepo.getTotalLeadTimeByProductSw(startDate, endDate, productCode, vendorCode, status.toUpperCase(), userId, areaId, bookingCode);
		
		return TotalTrxResponse.builder()
				.startDate(start)
				.endDate(end)
				.totalAllTrx(totatTrx)
				.build();
	}
	
	@SneakyThrows
	public Page<LeadTimeDetailResponse> getListDetailLeadTime(LeadTimeRequest req,Pageable pageable){
		log.info("===> Detail Lead Time <===");
		LocalDate startDate = LocalDate.parse(dateStart, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate endDate = LocalDate.now();
		if(req.getStartDate() != null && req.getEndDate() != null) {
			startDate = LocalDate.parse(req.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			endDate = LocalDate.parse(req.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		List<LeadTimeDetailResponse> result = new ArrayList<>();
		//Page<Object[]> lObject = tLeadTimeRepo.getDetailLeadTimeByProductSw(startDate, endDate, req.getProductCode(), req.getVendorCode(),req.getStatus(), req.getUserId(), req.getAreaId(), req.getBookingCode(), pageable);
		Page<TLeadTimeEntity> lLeadTime = tLeadTimeRepo.getDetailLeadTimeBySwitcher(startDate, endDate, req.getProductCode(), req.getVendorCode(), req.getStatus(), req.getUserId(), req.getAreaId(), req.getBookingCode(), pageable);
		for(TLeadTimeEntity leadTime : lLeadTime) {
			TPaymentEntity payment = leadTime.getBookingCode();
			MAreaEntity mAreaEntity = mAreaRepo.findByAreaName(payment.getOrigin());
			List<TAreaEntity> area = tAreaRepo.findTopByProductSwCodeProductSwCodeAndAreaIdIdPostalCodeAndAreaOriginIdOrderByTarifAsc(payment.getProductSwCode().getProductSwCode(), payment.getIdPostalCode().getIdPostalCode(), mAreaEntity.getAreaId());
			LeadTimeDetailResponse detail = LeadTimeDetailResponse.builder()
											.bookingCode(payment.getBookingCode())
											.userId(payment.getUserId().getUserId())
											.productName(payment.getProductSwCode().getName())
											.vendor(payment.getProductSwCode().getSwitcherEntity().getName())
											.resi(payment.getStt())
											.origin(payment.getOrigin())
											.destination(payment.getDestination())
											.receiverName(payment.getReceiverName())
											.tanggalBooking(payment.getTrxDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
											.tanggalKirim(leadTime.getTimeLeave()==null?"-":LocalDateTime.parse(leadTime.getTimeLeave(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
											.tanggalSampai(leadTime.getTimeArrived()==null?"-":LocalDateTime.parse(leadTime.getTimeArrived(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
											.estimasi(area.size()==0?"-":area.get(0).getStartDay()+"-"+area.get(0).getEndDay())
											.dueDays(leadTime.getTimeArrived()==null?"0":getDueDays(leadTime.getTimeLeave(),leadTime.getTimeArrived(),area.get(0).getEndDay()))
											.branchName(officeRepo.findAllByOfficeCode(payment.getOfficeCode()).getName())
											.estimasiReal(leadTime.getTimeArrived()==null?"0":getEstimasiReal(leadTime.getTimeLeave(), leadTime.getTimeArrived()).toString())
											.build();
			result.add(detail);
		}
		
		return new PageImpl<>(result, lLeadTime.getPageable(), lLeadTime.getTotalElements());
	}
	@SneakyThrows
	private String getDueDays(String timeLeave, String timeArrived, Integer endDay) {
		// TODO Auto-generated method stub
		Integer dtDue = getEstimasiReal(timeLeave, timeArrived);
		
		return String.valueOf(endDay-dtDue);
	}

	private Integer getEstimasiReal(String timeLeave, String timeArrived) throws ParseException {
		return DateTimeUtil.getDateFrom(timeArrived, "yyyy-MM-dd HH:mm").getDayOfYear() - DateTimeUtil.getDateFrom(timeLeave, "yyyy-MM-dd HH:mm").getDayOfYear();
	}

	@Async
	public SaveResponse getStatusTracking(String bookingCode,Integer vendorCode) {
		log.info("===> HIT Manual cron tracking By Booking Code "+bookingCode+"OR Vendor "+vendorCode);
		if(bookingCode==null && vendorCode==null)throw new InternalServerException("Tolong isi Kode Booking Atau Kode Vendor");
		String result = "";
		URI url = URI.create(urlcron);
		try {
			result = feignService.fetchManualCronTracking(url, bookingCode, vendorCode);
		}catch (FeignException e) {
			// TODO: handle exception
			log.error("Error HIT Cron tracking :  " +e.toString());
			e.printStackTrace();
			throw new InternalServerException("Error HIT Cron tracking :  " +e.toString());
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation(result)
				.build();
	}
}
