package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TOfficeEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.entity.TWarehouseReceiveDetailEntity;
import com.kahago.kahagoservice.entity.TWarehouseReceiveEntity;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.enummodel.WarehouseEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DepositBookRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TOfficeRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveDetailRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveRepo;

@Service
public class DepositBookService {

	@Autowired
	private TPickupOrderRequestRepo pickupOrderRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo pickupOrderDetailRepo;
	@Autowired
	private RequestPickUpService reqPickup;
	@Autowired
	private MUserRepo userRepo;
	@Autowired
	private TWarehouseReceiveDetailRepo whDetailRepo;
	@Autowired
	private TWarehouseReceiveRepo whRepo;
	@Autowired
	private TOfficeRepo officeRepo;
	@Autowired
	private BookCounterService bookCounterService;
	@Autowired
	private TPaymentRepo paymentRepo;
	@org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public SaveResponse doInsert(DepositBookRequest req,String userLogin) {
		MUserEntity user = userRepo.findById(req.getUserId()).orElseThrow(() -> new NotFoundException("User Tidak Ditemukan"));
		if(req.getQrcodes().size() == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tolong isi Qrcode terlebih dahulu !");
		List<TPickupOrderRequestDetailEntity> lsPORDetail = pickupOrderDetailRepo.findByQrcodeExtIn(req.getQrcodes());
		for(String qrCode :req.getQrcodes()) {
			if(checkQrCodeExt(bookCounterService.replaceRegexQrcodeExt(qrCode)))throw new NotFoundException("QrCode : "+qrCode +" sudah digunakan !");
		}
		if(lsPORDetail.size()>0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ada QRcode yang sama");
		TPickupOrderRequestEntity pickupOrderRequest = TPickupOrderRequestEntity.builder()
				.createBy(userLogin)
				.createDate(LocalDateTime.now())
				.orderDate(LocalDate.now())
				.qty(req.getQrcodes().size())
				.userEntity(user)
				.pickupOrderId(reqPickup.createOrderNumb())
				.status(RequestPickupEnum.IN_WAREHOUSE.getValue())
				.build();
		pickupOrderRepo.save(pickupOrderRequest);
		
		TOfficeEntity office = officeRepo.findByUserIdUserId(userLogin).stream().findAny().orElseThrow(() -> new NotFoundException("Office Code pada user login Tidak Ditemukan"));
		List<TPickupOrderRequestDetailEntity> lspickupOrderDetail = req.getQrcodes().stream()
				.map(p-> mapToDetail(p, pickupOrderRequest)).collect(Collectors.toList());
		reqPickup.saveAll(lspickupOrderDetail);
		TWarehouseReceiveEntity warehouseReceive = TWarehouseReceiveEntity.builder()
				.code(pickupOrderRequest.getPickupOrderId())
				.createBy(userLogin)
				.createDate(LocalDateTime.now())
				.officeCode(req.getOfficeCode()==null?office.getOfficeCode().getOfficeCode():req.getOfficeCode())
				.build();
		whRepo.saveAndFlush(warehouseReceive);
		List<TWarehouseReceiveDetailEntity> lsTWH = 
				lspickupOrderDetail.stream().map(pod -> toTWHDetail(pod, warehouseReceive)).collect(Collectors.toList());
		whDetailRepo.saveAll(lsTWH);
		return SaveResponse.builder()
				.linkResi("")
				.saveStatus(1)
				.saveInformation("Berhasil")
				.build();
	}
	
	private TWarehouseReceiveDetailEntity toTWHDetail(TPickupOrderRequestDetailEntity pod,TWarehouseReceiveEntity warehouseReceive) {
		return TWarehouseReceiveDetailEntity.builder()
				.createAt(LocalDateTime.now())
				.createBy(pod.getCreateBy())
				.status(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode())
				.warehouseReceiveId(warehouseReceive)
				.qrcodeRequest(pod.getQrcodeExt())
				.build();
	}
	private TPickupOrderRequestDetailEntity mapToDetail(String qrcode,TPickupOrderRequestEntity pickupOrder) {
		
		return TPickupOrderRequestDetailEntity.builder()
				.orderRequestEntity(pickupOrder)
				.createBy(pickupOrder.getCreateBy())
				.qrcodeExt(bookCounterService.replaceRegexQrcodeExt(qrcode))
				.qrCode(bookCounterService.replaceRegexQrcodeExt(qrcode))
				.qty(1)
				.amount(BigDecimal.ZERO)
				.isPay(StatusPayEnum.NOT_PAID.getCode())
				.status(RequestPickupEnum.IN_WAREHOUSE.getValue())
				.createDate(LocalDateTime.now())
				.build();
	}
	
	public Boolean checkQrCodeExt(String qrCodeExt) {
		TPickupOrderRequestDetailEntity pickupReq = pickupOrderDetailRepo.findByQrcodeExt(qrCodeExt);
		TPaymentEntity payment = paymentRepo.findByQrCodeExtOrQrcodeOrBookingCode(qrCodeExt);
		
		if(pickupReq != null || payment != null) {
			return true;
		}
		return false;
	}
}
