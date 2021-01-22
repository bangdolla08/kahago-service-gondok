package com.kahago.kahagoservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.TOutgoingListEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TWarehouseReceiveDetailEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.DetailStatus;
import com.kahago.kahagoservice.model.request.TotalTrxRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.repository.TOutgoingListRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TPickupRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveDetailRepo;

/**
 * @author Ibnu Wasis
 */
@Service
public class ApprovalBookingService {
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private TPickupOrderRequestRepo tOrderRequestRepo;
//	@Autowired
//	private TPickupDetailRepo tPickupDetailRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo tPickupOrderRequestDetailRepo;
	@Autowired
	private TPickupRepo tPickupRepo;
	@Autowired
	private OfficeCodeService officeService;
	@Autowired
	private TWarehouseReceiveDetailRepo tWarehouseReceiveDetailRepo;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	@Autowired
	private TOutgoingListRepo outgoingListRepo;
	@Autowired
	private TPaymentHistoryRepo tPaymentHistoryRepo;
	
	private static final Logger log = LoggerFactory.getLogger(ApprovalBookingService.class);
	
	public Page<BookDataResponse> getListAppBooking(Pageable pageable,String userId,Integer status,String bookingCode,String filter,Integer switcherCode){
		Page<TPaymentEntity> lpayment = tPaymentRepo.findAllByUserIdAndStatusAndBookingCode(userId, status, bookingCode,filter,switcherCode, pageable);
		return new PageImpl<>(
				lpayment.getContent().stream().map(this::toDto).collect(Collectors.toList()), 
				lpayment.getPageable(), 
				lpayment.getTotalElements());
	}
	
	public Page<BookDataResponse> getListAppBookingByStatusIn(Pageable pageable,String userId,
			List<Integer> status,String bookingCode,String filter,List<String> officeCode,
			List<Integer> switcherCode, String origin){
		Page<TPaymentEntity> lpayment = tPaymentRepo.findAllByUserIdAndStatusInAndBookingCode(userId, status, 
				bookingCode,filter, officeCode,switcherCode,origin,pageable);
		return new PageImpl<>(
				lpayment.getContent().stream().map(this::toDto).collect(Collectors.toList()), 
				lpayment.getPageable(), 
				lpayment.getTotalElements());
	}
	
	public Page<BookDataResponse> getListAppBookingByStatusInLate(Pageable pageable,String userId,
			List<Integer> status,String bookingCode,String filter,List<String> officeCode,
			List<Integer> switcherCode){
		Page<TPaymentEntity> lpayment = null;
		List<TPaymentEntity> lsPay = new ArrayList<>();
		for(String offices : officeCode) {
			TOutgoingListEntity outgoingList = outgoingListRepo
					.findFirstBySwitcherEntitySwitcherCodeAndOfficeCodeOfficeCodeOrderByIdOutgoingListDesc(switcherCode.get(0),offices);
			if(outgoingList!=null) {
				lsPay.addAll(tPaymentRepo.findAllByUserIdAndStatusInAndBookingCodeLate(userId, status, 
						bookingCode,filter, Arrays.asList(offices),switcherCode,outgoingList.getProcessTime(),outgoingList.getProcessDate()));
			}
			
		}
		lpayment = new PageImpl<>(lsPay, pageable, lsPay.size());
		
		return new PageImpl<>(
				lpayment.getContent().stream().map(this::toDto).collect(Collectors.toList()), 
				lpayment.getPageable(), 
				lpayment.getTotalElements());
	}
	
	public Page<BookDataResponse> getListAppBookingByStatusInWithoutOffice(Pageable pageable,String userId,
			List<Integer> status,String bookingCode,String filter,
			Integer switcherCode){
		Page<TPaymentEntity> lpayment = tPaymentRepo.findAllByUserIdAndStatusInAndBookingCode(userId, status, 
				bookingCode,filter, switcherCode,pageable);
		return new PageImpl<>(
				lpayment.getContent().stream().map(this::toDto).collect(Collectors.toList()), 
				lpayment.getPageable(), 
				lpayment.getTotalElements());
	}
	private BookDataResponse toDto(TPaymentEntity payment) {
		BookDataResponse result = paymentService.toBookDataResponse(payment);
		if(payment.getStatus() <= PaymentEnum.REQUEST.getCode())
			result.setReceiverPhone("");
		return result;
	}
//	private BookDataResponse toDto(TPaymentEntity payment) {		
//		return paymentService.toBookDataResponse(payment);
//>>>>>>> 8460813d... Additional : Barang Tititpan diberi inputan 3 digit nomor tlpn penerima yang paling akhir part 2
//	}
	
	public BookDataResponse getDetailBooking(String bookingCode) {
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		return toDto(payment);
	}
	
	public TotalTrxResponse getTotalTrx(TotalTrxRequest request){
		LocalDateTime startDate = LocalDateTime.now().minusMonths(2);
		LocalDateTime endDate = LocalDateTime.now();
		String time =" 00:00";
		/*if(request.getStartDate()!=null && request.getEndDate() != null) {
			try {
				startDate = DateTimeUtil.getDateFromString(request.getStartDate()+time, "yyyyMMdd HH:mm");
				endDate = DateTimeUtil.getDateFromString(request.getEndDate()+time, "yyyyMMdd HH:mm");			
			}catch (ParseException e) {
				// TODO: handle exception
				log.error(e.getMessage());
				e.printStackTrace();
			}
			
		}*/
		List<Integer> status = new ArrayList<Integer>();
		List<Integer> statusReq = new ArrayList<>();
		List<String> officeCode = null;
		for(DetailStatus st:request.getStatus()) {
			status.add(st.getStatus());
		}
		for(DetailStatus st:request.getStatus()) {
			Integer statuss=RequestPickupEnum.toPaymentEnumInteger(st.getStatus());
			if(statuss!=null){
				statusReq.add(statuss);
			}
		}
		if(request.getOfficeCode() != null) {
			officeCode = new ArrayList<>();
//			for(OfficeCodeIdRequest ofc : request.getOfficeCode()) {
//				officeCode.add(ofc.getOfficeCode());
//			}
			officeCode = officeService
					.getBranchList(request.getOfficeCode().stream().findFirst().get().getOfficeCode())
					.stream().map(p->p.getOfficeCode()).collect(Collectors.toList());
		}
		Integer totalTrxBook = tPaymentRepo.countByUserIdAndStatusAndTrxDate(status,startDate.toLocalDate(),endDate.toLocalDate(), officeCode);
		Integer totalTrxReq=0;
		if(statusReq.size() > 0) {
			for(Integer st : statusReq) {
				if(st < RequestPickupEnum.IN_COURIER.getValue() && st == RequestPickupEnum.DRAFT_PICKUP.getValue()) {
					totalTrxReq = totalTrxReq + tOrderRequestRepo.countByUserAndStatusAndCreateDate(st,startDate,endDate);
				}else
					totalTrxReq = totalTrxReq + tPickupOrderRequestDetailRepo.countByStatusAndCreateDate(st, startDate, endDate);
			}
			
		}		
		
		return TotalTrxResponse.builder()
				.userId(request.getUserId())
				.status(status)
				.totalTrxBooking(totalTrxBook==null?0:totalTrxBook)
				.totalTrxRequest(totalTrxReq==null?0:totalTrxReq)
				.build();
	}

	public Page<BookDataResponse> getListBookingComplete(Pageable pageable,String userId,String bookingCode,String filter,Integer switcherCode){
		Page<TPaymentEntity> lpayment = tPaymentRepo.findAllByUserIdAndStatusAndBookingCode(userId, PaymentEnum.UNPAID_RECEIVE.getCode(), bookingCode,filter,switcherCode, pageable);
		
		return new PageImpl<>(
				lpayment.stream().map(this::toDto).collect(Collectors.toList()),
				lpayment.getPageable(),
				lpayment.getTotalElements());
	}
	
	public TotalTrxResponse getTotalAllTrx(TotalTrxRequest request) {
		List<Integer> status = new ArrayList<Integer>();
		List<Integer> statusReq = new ArrayList<>();
		List<String> officeCode = null;
		for(DetailStatus st:request.getStatus()) {
			status.add(st.getStatus());
		}
		for(DetailStatus st:request.getStatus()) {
			Integer statuss=RequestPickupEnum.toPaymentEnumInteger(st.getStatus());
			if(statuss!=null){
				statusReq.add(statuss);
			}
		}
		if(request.getOfficeCode() != null ) {
			officeCode = new ArrayList<>();
//			for(OfficeCodeIdRequest ofc : request.getOfficeCode()) {
//				officeCode.add(ofc.getOfficeCode());
//			}
			officeCode = officeService
					.getBranchList(request.getOfficeCode().stream().findFirst().get().getOfficeCode())
					.stream().map(p->p.getOfficeCode()).collect(Collectors.toList());
		}
		Integer totalpayment = tPaymentRepo.countByStatusAndSwitcherCodeAndOfficeCode(status, request.getSwitcherCode(), officeCode);
		Integer totalReq = tOrderRequestRepo.countByStatusIn(statusReq);
		Integer total = totalpayment+totalReq;
		return TotalTrxResponse.builder()
				.userId(request.getUserId())
				.totalAllTrx(total==null?0:total)
				.totalTrxRequest(totalReq==null?0:totalReq)
				.totalTrxBooking(totalpayment==null?0:totalpayment)
				.build();
	}
	
	public TotalTrxResponse getTotalAllTrxLate(TotalTrxRequest request) {
		List<Integer> status = new ArrayList<Integer>();
		List<Integer> statusReq = new ArrayList<>();
		List<String> officeCode = null;
		for(DetailStatus st:request.getStatus()) {
			status.add(st.getStatus());
		}
		for(DetailStatus st:request.getStatus()) {
			Integer statuss=RequestPickupEnum.toPaymentEnumInteger(st.getStatus());
			if(statuss!=null){
				statusReq.add(statuss);
			}
		}
		if(request.getOfficeCode() != null ) {
			officeCode = new ArrayList<>();

			officeCode = officeService
					.getBranchList(request.getOfficeCode().stream().findFirst().get().getOfficeCode())
					.stream().map(p->p.getOfficeCode()).collect(Collectors.toList());
		}
		Integer totalpayment = 0;
//		List<TOutgoingListEntity> outgoingList = outgoingListRepo.findByStatusLate(request.getSwitcherCode(), officeCode);
		for(String office:officeCode) {
			TOutgoingListEntity outgoing = outgoingListRepo.
					findFirstBySwitcherEntitySwitcherCodeAndOfficeCodeOfficeCodeOrderByIdOutgoingListDesc(request.getSwitcherCode(), office);
			if(outgoing!=null) {
				totalpayment += tPaymentRepo.countByStatusAndSwitcherCodeAndOfficeCodeAndProcessTime(status,
						request.getSwitcherCode(),Arrays.asList(office),outgoing.getProcessDate(),outgoing.getProcessTime());
			}
		}
		
		Integer totalReq = 0;
		Integer total = totalpayment+totalReq;
		return TotalTrxResponse.builder()
				.userId(request.getUserId())
				.totalAllTrx(total==null?0:total)
				.totalTrxRequest(totalReq==null?0:totalReq)
				.totalTrxBooking(totalpayment==null?0:totalpayment)
				.build();
	}
	
	public TotalTrxResponse getAllBookingUnComplete(TotalTrxRequest request) {
		List<Integer> status = new ArrayList<Integer>();
		for(DetailStatus st:request.getStatus()) {
			status.add(st.getStatus());
		}
		Integer total = tPickupOrderRequestDetailRepo.countTotalBookingUnComplete(status);
		return TotalTrxResponse.builder()
				.userId(request.getUserId())
				.totalAllTrx(total==null?0:total)
				.build();
	}
	
	public TotalTrxResponse getTotalManifest(TotalTrxRequest request) {
		List<Integer> status = new ArrayList<Integer>();
		for(DetailStatus st:request.getStatus()) {
			status.add(st.getStatus());
		}
		Integer totalManifest = tPickupRepo.countByStatusIn(status);
		
		return TotalTrxResponse.builder()
				.userId(request.getUserId())
				.totalAllTrx(totalManifest==null?0:totalManifest)
				.build();
	}
	@Transactional
	public SaveResponse doResetBookingByBookingCode(String bookingCode,String reason, String user) {
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		if(payment==null)throw new NotFoundException("Pesanan tidak Ditemukan !");
		TPaymentEntity oldPayment = paymentService.createOldPayment(payment);
		TPickupOrderRequestDetailEntity orderDetail = tPickupOrderRequestDetailRepo.findByQrcodeExt(payment.getQrcodeExt());
		if(orderDetail==null)throw new NotFoundException("Pesanan tidak Ditemukan !");
		TWarehouseReceiveDetailEntity warehouseDetail = tWarehouseReceiveDetailRepo.findByQrcodeRequest(payment.getQrcodeExt()).orElseThrow(()-> new NotFoundException("Data Tidak Ditemukan in warehouse"));
		orderDetail.setStatus(RequestPickupEnum.IN_WAREHOUSE.getValue());
		orderDetail.setBookCode("");
		orderDetail.setProductSwitcherEntity(null);
		orderDetail.setAreaId(null);
		orderDetail.setNamaPenerima("");
		orderDetail.setWeight(Double.valueOf("1"));
		if(orderDetail.getOrderRequestEntity().getStatus() >= RequestPickupEnum.FINISH_BOOK.getValue())
			orderDetail.getOrderRequestEntity().setStatus(RequestPickupEnum.IN_WAREHOUSE.getValue());
		warehouseDetail.setBookId(null);
		payment.setStatus(PaymentEnum.CANCEL_BY_ADMIN.getValue());
		payment.setQrcodeExt("");
		tPickupOrderRequestDetailRepo.save(orderDetail);
		tOrderRequestRepo.save(orderDetail.getOrderRequestEntity());
		tWarehouseReceiveDetailRepo.save(warehouseDetail);
		tPaymentRepo.save(payment);
		historyTransactionService.createHistory(oldPayment, payment, user);
		TPaymentHistoryEntity pHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(payment, PaymentEnum.CANCEL_BY_ADMIN.getValue());
		pHistory.setReason(reason);
		tPaymentHistoryRepo.save(pHistory);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Reset Pesanan !")
				.build();
	}
}
