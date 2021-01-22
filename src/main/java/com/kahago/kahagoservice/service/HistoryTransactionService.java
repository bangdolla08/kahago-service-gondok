package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderHistoryRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TBookDetailHistoryRepo;
import com.kahago.kahagoservice.model.dto.*;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TBookDetailHistoryEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupOrderHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bangd ON 27/11/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class HistoryTransactionService {
    @Autowired
    private TPaymentHistoryRepo paymentHistoryRepo;
    @Autowired
    private THistoryBookRepo historyBookRepo;
    @Autowired
    private TBookDetailHistoryRepo bookDetailHistoryRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private TPickupOrderHistoryRepo tPickupOrderHistoryRepo;
    private UserDto userDto;

    @Transactional
    public Boolean createHistory(TPaymentEntity oldPaymentEntity,TPaymentEntity newPaymentEntity,String userId){
        userDto=userService.getMUserEntity(userId);
        Integer isRefund = 1;
        if(newPaymentEntity.getStatus().equals(PaymentEnum.APPROVE_BY_COUNTER.getValue()) ||
        		newPaymentEntity.getStatus().equals(PaymentEnum.ACCEPT_IN_WAREHOUSE.getValue())) {
        	isRefund = 0;
        }
        TPaymentHistoryEntity paymentHistoryEntity= TPaymentHistoryEntity
                .builder()
                .bookingCode(newPaymentEntity)
                .userId(userId)
                .amount(oldPaymentEntity.getAmount())
                .trxServer(newPaymentEntity.getTrxServer())
                .jumlahLembar(newPaymentEntity.getJumlahLembar())
                .insurance(oldPaymentEntity.getInsurance())
                .extraCharge(oldPaymentEntity.getExtraCharge())
                .price(oldPaymentEntity.getPrice())
                .priceKg(oldPaymentEntity.getPriceKg())
                .priceRepack(oldPaymentEntity.getPriceRepack())
                .lastInsurance(newPaymentEntity.getInsurance().intValue())
                .lastAmount(newPaymentEntity.getAmount())
                .lastPrice(newPaymentEntity.getPrice())
                .lastPriceKg(newPaymentEntity.getPriceKg())
                .lastPriceRepack(newPaymentEntity.getPriceRepack())
                .grossWeight(oldPaymentEntity.getGrossWeight())
                .lastGrossWeight(newPaymentEntity.getGrossWeight())
                .lastExtraCharge(newPaymentEntity.getExtraCharge().intValue())
                .volume(oldPaymentEntity.getVolume())
                .lastVolume(newPaymentEntity.getVolume())
                .status(oldPaymentEntity.getStatus())
                .lastStatus(newPaymentEntity.getStatus())
                .totalPackKg(newPaymentEntity.getTotalPackKg().intValue())
                .lastUser(userId)
                .lastUpdate(LocalDateTime.now())
                .isRefund(isRefund)
                .build();
        paymentHistoryEntity=paymentHistoryRepo.save(paymentHistoryEntity);
        for(TBookEntity bookEntity:newPaymentEntity.getTbooks()){
            TBookEntity oldTBookEntity=oldPaymentEntity.getTbooks().stream().filter((p)->p.getSeqid()==bookEntity.getSeqid()).findAny().orElse(null);
            if(oldTBookEntity!=null) {
                TBookDetailHistoryEntity detailHistoryEntity =historyBook(oldTBookEntity,bookEntity);
                detailHistoryEntity.setPaymentHistory(paymentHistoryEntity);
                bookDetailHistoryRepo.save(detailHistoryEntity);
            }
        }
        return true;
    }

    public Boolean createHistory(TPaymentEntity oldPaymentEntity,PaymentDto newPaymentdto,String userId){
        TPaymentEntity newPaymentEntity=newPaymentdto.getPaymentEntity();
        newPaymentEntity.setStatus(newPaymentdto.getPaymentEnum().getValue());
        return this.createHistory(oldPaymentEntity,newPaymentdto.getPaymentEntity(),userId);
    }


    private TBookDetailHistoryEntity historyBook(TBookEntity oldBookEntity,TBookEntity bookEntity){
        Integer count=paymentHistoryRepo.countAllByBookingCodeBookingCode(oldBookEntity.getBookingCode());
        return TBookDetailHistoryEntity .builder()
                .bookingCode(bookEntity.getBookingCode())
                .length(oldBookEntity.getLength())
                .lastLength(bookEntity.getLength())
                .width(oldBookEntity.getWidth())
                .lastWidth(bookEntity.getWidth())
                .height(oldBookEntity.getHeight())
                .lastHeight(bookEntity.getHeight())
                .grossWeight(oldBookEntity.getGrossWeight())
                .lastGrossWeight(bookEntity.getGrossWeight())
                .volWeight(oldBookEntity.getVolWeight())
                .lastVolWeight(bookEntity.getVolWeight())
                .counterChanges(count.toString())
                .tglSystem(LocalDateTime.now())
                .updateUserid(userDto.getMUserEntity().getUserId())
                .build();
    }

	@Transactional
	public Boolean createHistoryRequest(TPaymentEntity oldPaymentEntity,TPaymentEntity newPaymentEntity,String userId,TPickupOrderRequestDetailEntity orderRequestEntity){
	    userDto=userService.getMUserEntity(userId);
	    TPaymentHistoryEntity paymentHistoryEntity= TPaymentHistoryEntity
	            .builder()
	            .bookingCode(newPaymentEntity)
	            .userId(userId)
	            .amount(orderRequestEntity.getAmount())
	            .lastAmount(newPaymentEntity.getAmount())
	            .trxServer(newPaymentEntity.getTrxServer())
	            .jumlahLembar(newPaymentEntity.getJumlahLembar())
	            .insurance(newPaymentEntity.getInsurance())
	            .extraCharge(oldPaymentEntity.getExtraCharge())
	            .price(oldPaymentEntity.getPrice())
	            .priceKg(oldPaymentEntity.getPriceKg())
	            .priceRepack(oldPaymentEntity.getPriceRepack())
	            .lastPrice(newPaymentEntity.getPrice())
	            .lastPriceKg(newPaymentEntity.getPriceKg())
	            .lastPriceRepack(newPaymentEntity.getPriceRepack())
	            .grossWeight(oldPaymentEntity.getGrossWeight())
	            .lastGrossWeight(newPaymentEntity.getGrossWeight())
	            .volume(oldPaymentEntity.getVolume())
	            .lastVolume(newPaymentEntity.getVolume())
	            .status(oldPaymentEntity.getStatus())
	            .lastStatus(newPaymentEntity.getStatus())
	            .lastUser(userId)
	            .lastUpdate(LocalDateTime.now())
	            .build();
	    paymentHistoryEntity=paymentHistoryRepo.save(paymentHistoryEntity);
	    for(TBookEntity bookEntity:newPaymentEntity.getTbooks()){
	        TBookEntity oldTBookEntity=oldPaymentEntity.getTbooks().stream().filter((p)->p.getSeqid()==bookEntity.getSeqid()).findAny().orElse(null);
	        if(oldTBookEntity!=null) {
	            TBookDetailHistoryEntity detailHistoryEntity =historyBook(oldTBookEntity,bookEntity);
	            detailHistoryEntity.setPaymentHistory(paymentHistoryEntity);
	            bookDetailHistoryRepo.save(detailHistoryEntity);
	        }
	    }
	    return true;
	}
	
	public Boolean createHistoryBook(List<TBookEntity> tbookOld,List<TBookEntity> tbookNew,TPaymentEntity payment) {
		TPaymentHistoryEntity paymentHistory = paymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByTrxServerDesc(payment, PaymentEnum.HOLD_BY_WAREHOUSE.getCode());
		List<TBookDetailHistoryEntity> lBookHistory = bookDetailHistoryRepo.findByPaymentHistory(paymentHistory);
		int seq = 0;
		for(TBookDetailHistoryEntity bookDetail : lBookHistory) {
			bookDetail.setGrossWeight(tbookOld.get(seq).getGrossWeight());
			bookDetail.setHeight(tbookOld.get(seq).getHeight());
			bookDetail.setVolWeight(tbookOld.get(seq).getVolWeight());
			bookDetail.setLength(tbookOld.get(seq).getLength());
			bookDetail.setWidth(tbookOld.get(seq).getWidth());
			bookDetail.setLastGrossWeight(tbookNew.get(seq).getGrossWeight());
			bookDetail.setLastHeight(tbookNew.get(seq).getHeight());
			bookDetail.setLastLength(tbookNew.get(seq).getLength());
			bookDetail.setLastVolWeight(tbookNew.get(seq).getVolWeight());
			bookDetail.setLastWidth(tbookNew.get(seq).getWidth());
		}
		bookDetailHistoryRepo.saveAll(lBookHistory);
		return true;
	}
	
	public List<TBookEntity> createoldTbook(List<TBookEntity> ltbook){
		List<TBookEntity> result = new ArrayList<TBookEntity>();
		for(TBookEntity tb : ltbook) {
			TBookEntity book = new TBookEntity();
			book.setBookingCode(tb.getBookingCode());
			book.setGrossWeight(tb.getGrossWeight());
			book.setHeight(tb.getHeight());
			book.setLength(tb.getLength());
			book.setVolWeight(tb.getVolWeight());
			book.setWidth(tb.getWidth());
			result.add(book);
		}
		return result;
	}
	
	@Transactional
    public Boolean createHistory(TPaymentEntity oldPaymentEntity,TPaymentEntity newPaymentEntity,String userId,String reason){
        userDto=userService.getMUserEntity(userId);
        Integer isRefund = 1;
        if(newPaymentEntity.getStatus().equals(PaymentEnum.APPROVE_BY_COUNTER.getValue()) ||
        		newPaymentEntity.getStatus().equals(PaymentEnum.ACCEPT_IN_WAREHOUSE.getValue())) {
        	isRefund = 0;
        }
        TPaymentHistoryEntity paymentHistoryEntity= TPaymentHistoryEntity
                .builder()
                .bookingCode(newPaymentEntity)
                .userId(userId)
                .amount(oldPaymentEntity.getAmount())
                .trxServer(newPaymentEntity.getTrxServer())
                .jumlahLembar(newPaymentEntity.getJumlahLembar())
                .insurance(oldPaymentEntity.getInsurance())
                .extraCharge(oldPaymentEntity.getExtraCharge())
                .price(oldPaymentEntity.getPrice())
                .priceKg(oldPaymentEntity.getPriceKg())
                .priceRepack(oldPaymentEntity.getPriceRepack())
                .lastInsurance(newPaymentEntity.getInsurance().intValue())
                .lastAmount(newPaymentEntity.getAmount())
                .lastPrice(newPaymentEntity.getPrice())
                .lastPriceKg(newPaymentEntity.getPriceKg())
                .lastPriceRepack(newPaymentEntity.getPriceRepack())
                .grossWeight(oldPaymentEntity.getGrossWeight())
                .lastGrossWeight(newPaymentEntity.getGrossWeight())
                .lastExtraCharge(newPaymentEntity.getExtraCharge().intValue())
                .volume(oldPaymentEntity.getVolume())
                .lastVolume(newPaymentEntity.getVolume())
                .status(oldPaymentEntity.getStatus())
                .lastStatus(newPaymentEntity.getStatus())
                .totalPackKg(newPaymentEntity.getTotalPackKg().intValue())
                .lastUser(userId)
                .lastUpdate(LocalDateTime.now())
                .isRefund(isRefund)
                .build();
        paymentHistoryEntity=paymentHistoryRepo.save(paymentHistoryEntity);
        for(TBookEntity bookEntity:newPaymentEntity.getTbooks()){
            TBookEntity oldTBookEntity=oldPaymentEntity.getTbooks().stream().filter((p)->p.getSeqid()==bookEntity.getSeqid()).findAny().orElse(null);
            if(oldTBookEntity!=null) {
                TBookDetailHistoryEntity detailHistoryEntity =historyBook(oldTBookEntity,bookEntity);
                detailHistoryEntity.setPaymentHistory(paymentHistoryEntity);
                bookDetailHistoryRepo.save(detailHistoryEntity);
            }
        }
        return true;
    }
	public Boolean historyRequestPickup(TPickupOrderRequestEntity pickupReq, TPickupOrderRequestDetailEntity pickupReqDtl, Integer status, String userId, String reason) {
		TPickupOrderHistoryEntity entity = new TPickupOrderHistoryEntity();
		entity.setPickupOrderId(pickupReq.getPickupOrderId());
		if(pickupReqDtl != null) {
			entity.setPickupOrderDetailId(pickupReqDtl.getSeq());
			entity.setLastStatus(pickupReqDtl.getStatus());
		}else {
			entity.setLastStatus(pickupReq.getStatus());
		}
		entity.setStatus(status);
		entity.setCreatedBy(userId);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setReason(reason);
		tPickupOrderHistoryRepo.save(entity);
		
		return true;
	}
	
	public TPaymentHistoryEntity getPaymentHistory(TPaymentEntity payment, Integer status, Integer lastStatus) {
		List<TPaymentHistoryEntity> payHistory = paymentHistoryRepo.findFirstByBookingCodeAndStatusAndLastStatusOrderByTrxServerDesc(payment, status, lastStatus);
		
		return payHistory.get(0);
	}
	
	public void saveHistory(TPaymentHistoryEntity entity) {
		paymentHistoryRepo.save(entity);
	}
}
