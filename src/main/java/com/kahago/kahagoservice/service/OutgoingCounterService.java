package com.kahago.kahagoservice.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.client.model.request.Payment;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.TOutGoingCounterEnum;
import com.kahago.kahagoservice.enummodel.TOutGoingEnum;
import com.kahago.kahagoservice.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.MCounterEntity;
import com.kahago.kahagoservice.entity.TOfficeEntity;
import com.kahago.kahagoservice.entity.TOutgoingCounterDetailEntity;
import com.kahago.kahagoservice.entity.TOutgoingCounterEntity;
import com.kahago.kahagoservice.entity.TOutgoingListDetailEntity;
import com.kahago.kahagoservice.entity.TOutgoingListEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.OutGoingCounterEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.request.AppBookingRequest;
import com.kahago.kahagoservice.model.request.DetailStatus;
import com.kahago.kahagoservice.model.request.OfficeCodeIdRequest;
import com.kahago.kahagoservice.model.request.OutgoingCounterReq;
import com.kahago.kahagoservice.model.request.OutgoingRequest;
import com.kahago.kahagoservice.model.request.SaveCourierReq;
import com.kahago.kahagoservice.model.request.TotalTrxRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.OutgoingResponse;
import com.kahago.kahagoservice.model.response.RespUncomplete;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.repository.MCounterRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.TOfficeRepo;
import com.kahago.kahagoservice.repository.TOutgoingCounterDetailRepo;
import com.kahago.kahagoservice.repository.TOutgoingCounterRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TPickupRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Service
public class OutgoingCounterService {
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private HistoryTransactionService historyService;
	@Autowired
    private MCounterRepo counterRepo;
	@Autowired
	private TOutgoingCounterRepo outgoingCounterRepo;
	@Autowired
	private MOfficeRepo officeRepo;
	@Autowired
	private TOfficeRepo tofficeRepo;
	@Autowired
	private TOutgoingCounterDetailRepo outgoingCounterDetailRepo;
	private static final Logger log = LoggerFactory.getLogger(OutgoingCounterService.class);
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public OutgoingResponse create(OutgoingRequest request, String name) {
		// TODO Auto-generated method stub
		log.info("==> Create Outgoing Counter <==");
		log.info(Common.json2String(request));
		TOutgoingCounterEntity outgoing = null;
		if(request.getOutgoingNumber()==null) {
			outgoing = outgoingCounterRepo
					.findFirstByCreateByAndStatusAndCreateDate(name,
							request.getOfficeCode(),
							OutGoingCounterEnum.BAGGING.getCode(), 
							LocalDate.now())
					.orElse(TOutgoingCounterEntity.builder()
							.codeCounter(getCodeOutgoingCounter(request.getOfficeCode()))
							.courierId("")
							.createBy(name)
							.status(OutGoingCounterEnum.BAGGING.getCode())
							.createDate(LocalDate.now())
							.officeCode(officeRepo.findById(request.getOfficeCode()).get())
							.build()); 
			outgoingCounterRepo.save(outgoing);
		}else {
			outgoing = outgoingCounterRepo.findByCodeCounter(request.getOutgoingNumber()).get();
		}
		
		List<TOutgoingCounterDetailEntity> listDetailEntities = outgoingCounterDetailRepo.findByOutgoingCounterId(outgoing);
		return generateOutgoingResponse(outgoing, listDetailEntities);
	}
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public OutgoingResponse createDetail(String code,OutgoingRequest outgoing,String userid) {
		log.info("==> Create Outgoing Counter Detail <==");
		log.info(Common.json2String(outgoing));
		TOutgoingCounterEntity tout = outgoingCounterRepo.findByCodeCounterAndStatus(code,OutGoingCounterEnum.BAGGING.getCode()).orElseThrow(()-> new NotFoundException("Outgoing tidak ditemukan"));
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeAndStatusAndOfficeCode(outgoing.getBookingCode(),
				PaymentEnum.APPROVE_BY_COUNTER.getCode(),outgoing.getOfficeCode()).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Qrcode atau kode booking Tidak Ditemukan"));
		PaymentDto payDto = PaymentDto.builder()
				.bookCode(payment.getBookingCode())
				.paymentEnum(PaymentEnum.BAGGING_BY_COUNTER)
				.paymentEntity(payment).build();
		historyService.createHistory(payment, payDto, userid);
		TOutgoingCounterDetailEntity lsOutgoing = TOutgoingCounterDetailEntity.builder()
				.bookingCode(payment)
				.outgoingCounterId(tout)
				.status(OutGoingCounterEnum.BAGGING.getCode())
				.updateBy(userid)
				.updateDate(LocalDate.now())
				.build();
		payment.setStatus(PaymentEnum.BAGGING_BY_COUNTER.getCode());
		tPaymentRepo.save(payment);
		outgoingCounterDetailRepo.save(lsOutgoing);
		List<TOutgoingCounterDetailEntity> listDetailEntities = outgoingCounterDetailRepo.findByOutgoingCounterId(tout);
		return generateOutgoingResponse(tout, listDetailEntities);
	}
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public OutgoingResponse deleteDetail(String code,OutgoingRequest outgoing,String userid) {
		log.info("==> Delete Outgoing Counter Detail <==");
		log.info(Common.json2String(outgoing));
		TOutgoingCounterEntity tout = outgoingCounterRepo.findByCodeCounterAndStatus(code,OutGoingCounterEnum.BAGGING.getCode()).orElseThrow(()-> new NotFoundException("Outgoing tidak ditemukan"));
		TOutgoingCounterDetailEntity toutDetail = outgoingCounterDetailRepo.findByBookingCodeBookingCodeAndOutgoingCounterId(outgoing.getBookingCode(),tout);
		outgoingCounterDetailRepo.delete(toutDetail);
		List<TOutgoingCounterDetailEntity> listDetailEntities = outgoingCounterDetailRepo.findByOutgoingCounterId(tout);
		return generateOutgoingResponse(tout, listDetailEntities);
	}
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public OutgoingResponse prosesDetail(SaveCourierReq outgoing,String userid) {
		log.info("==> Proses Counter Outgoing <==");
		log.info(Common.json2String(outgoing));
		TOutgoingCounterEntity tout = outgoingCounterRepo.findByCodeCounterAndStatus(outgoing.getOutgoingCode(),TOutGoingCounterEnum.BAGGING.getCode()).orElseThrow(()-> new NotFoundException("Outgoing tidak ditemukan Atau Sudah Terproses"));
		tout.setCourierId(outgoing.getCourierId());
		tout.setStatus(TOutGoingCounterEnum.OUTGOING_COUNTER.getCode());
		outgoingCounterRepo.save(tout);
		
		List<TOutgoingCounterDetailEntity> listDetailEntities = outgoingCounterDetailRepo.findByOutgoingCounterId(tout);
		List<TPaymentEntity> lspay = listDetailEntities.stream().map(p->p.getBookingCode()).collect(Collectors.toList());
		lspay.forEach(savePay(userid));
		tPaymentRepo.saveAll(lspay);
		listDetailEntities.forEach(p->p.setStatus(OutGoingCounterEnum.OUTGOING.getCode()));
		outgoingCounterDetailRepo.saveAll(listDetailEntities);
		return generateOutgoingResponse(tout, listDetailEntities);
	}

	private Consumer<? super TPaymentEntity> savePay(String userid) {
		return p->{
			TPaymentEntity oldpay = paymentService.createOldPayment(p);
			p.setStatus(PaymentEnum.OUTGOING_BY_COUNTER.getCode());
			historyService.createHistory(oldpay, p, userid);
		};
	}
	public Page<OutgoingResponse> getListPage(Pageable pageable,OutgoingRequest req){
		Page<TOutgoingCounterEntity> out = outgoingCounterRepo.findByCreateBy(req.getUserId(), req.getOfficeCode(), pageable);
		 return new PageImpl<>(
	                out.getContent().stream().map(this::generateOutgoingResponse).collect(Collectors.toList()),
	                out.getPageable(),
	                out.getTotalElements());
	}
	private OutgoingResponse generateOutgoingResponse(TOutgoingCounterEntity outgoing){
		List<TOutgoingCounterDetailEntity> lsOut = outgoingCounterDetailRepo.findByOutgoingCounterId(outgoing);
		Long countItem = lsOut.stream().count();
		Long sumVol = lsOut.stream().mapToLong(p->p.getBookingCode().getVolume()).sum();
		Long sumWeight = lsOut.stream().mapToLong(p->p.getBookingCode().getGrossWeight()).sum();
        return OutgoingResponse.builder().codeOutgoing(outgoing.getCodeCounter())
                .officeName(outgoing.getOfficeCode().getName())
                .qtyItem(countItem.intValue())
                .sumVolume(sumVol.intValue())
                .sumWeight(sumWeight.intValue())
                .officeName(outgoing.getOfficeCode().getName())
                .officeCode(outgoing.getOfficeCode().getOfficeCode())
                .vendorName("")
                .vendorCode(0)
                .isEditable(CommonConstant.toBoolean(outgoing.getStatus()>1?1:outgoing.getStatus()))
                .statusOutgoing(outgoing.getStatus())
                .statusOutgoingString(TOutGoingEnum.getPaymentEnum(outgoing.getStatus()).toString())
                .dateOutgoing(outgoing.getCreateDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                .courierId(outgoing.getCourierId()==null?"":outgoing.getCourierId())
                .courierPhone("")
                .courierName("")
                .isPickupVendor(CommonConstant.toBoolean(outgoing.getStatus()>1?1:outgoing.getStatus()))
                .build();
    }
	private TOutgoingCounterDetailEntity toMap(String books,TOutgoingCounterEntity outgoing,String userid) {
		TOutgoingCounterDetailEntity out = TOutgoingCounterDetailEntity.builder()
				.bookingCode(tPaymentRepo.getOne(books))
				.outgoingCounterId(outgoing)
				.status(OutGoingCounterEnum.BAGGING.getCode())
				.updateBy(userid)
				.updateDate(LocalDate.now())
				.build();
		return out;
	}
	private String getCodeOutgoingCounter(String office) {
		String prefix = "COUT"+office;
		MCounterEntity counter = counterRepo.findAll().stream().findFirst().get();
		Integer count = counter.getOutgoingCounter();
		count++;
		prefix+=count.toString();
		counter.setOutgoingCounter(count);
		counterRepo.save(counter);
		return prefix;
	}
	
	private OutgoingResponse generateOutgoingResponse(TOutgoingCounterEntity outgoingListEntity, List<TOutgoingCounterDetailEntity> listDetailEntities){
        OutgoingResponse outgoingResponse=generateOutgoingResponse(outgoingListEntity);
        if(listDetailEntities!=null) {
            List<BookDataResponse> bookDataResponses = new ArrayList<>();
            for (TOutgoingCounterDetailEntity outgoingListDetailEntity : listDetailEntities) {
                bookDataResponses.add(paymentService.toBookDataResponse(outgoingListDetailEntity.getBookingCode()));
            }
            outgoingResponse.setBookDataResponses(bookDataResponses);
        }
        return outgoingResponse;
    }
	public BookDataResponse getDetailBooking(String bookingCode,String officeCode) {
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeAndStatusAndOfficeCode(bookingCode,
				PaymentEnum.APPROVE_BY_COUNTER.getCode(),officeCode).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Kode Booking atau Office Code Tidak Ditemukan"));
		return paymentService.toBookDataResponse(payment);
	}
}
