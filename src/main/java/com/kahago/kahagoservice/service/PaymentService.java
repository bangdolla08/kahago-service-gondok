package com.kahago.kahagoservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.FilterBookingEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.request.HistoryBookRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.PickupProperty;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.kahago.kahagoservice.util.ImageConstant.*;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 18 Nov 2019
 */
@Service
public class PaymentService {
	@Autowired
	private TPaymentRepo paymentRepo;
	@Autowired
	private MOfficeRepo officeRepo;
	@Autowired
	private AreaService areaService;
	@Autowired
	private OfficeCodeService officeCodeService;
	@Autowired
	private HistoryTransactionService histService;
	@Autowired
	private PickupService pickupService;
	private static final int USER_CATEGORY_COUNTER = 0;
	public TPaymentEntity save(TPaymentEntity payment) {
		return paymentRepo.save(payment);
	}

	public TPaymentEntity saveAndFlush(TPaymentEntity payment) {
		return paymentRepo.saveAndFlush(payment);
	}
	public TPaymentEntity get(String bookCode) {
		return paymentRepo.findByBookingCodeIgnoreCaseContaining(bookCode);
	}
	public List<TPaymentEntity> get(List<String> bookCode) {
		return paymentRepo.findByBookingCodeInIgnoreCaseContaining(bookCode);
	}
	public TPaymentEntity getBookAndStatusAndUserId(String bookCode,Integer status,String userId) {
		return paymentRepo.findByBookingCodeAndStatusAndUserIdUserId(bookCode, status, userId);
	}
	
	public TPaymentEntity getBookAndStatusAndUserId(String bookCode,Integer status) {
		return paymentRepo.findByBookingCodeInAndStatusIn(Arrays.asList(bookCode), Arrays.asList(status))
				.stream().findAny().orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Kode booking tidak ditemukan"));
	}
	public void saveAll(List<TPaymentEntity> payAll) {
		paymentRepo.saveAll(payAll);
	}
	public Page<BookDataResponse> historyBook(HistoryBookRequest historyBook) throws ParseException {
		LocalDate startDate= DateTimeUtil.getDateFrom(historyBook.getStartDate(),"yyyyMMdd");
		LocalDate endDate= DateTimeUtil.getDateFrom(historyBook.getEndDate(),"yyyyMMdd");
		FilterBookingEnum filterBookingEnum=FilterBookingEnum.getFilterBookingEnum(historyBook.getFilterBy());
		Page<TPaymentEntity> entityPage=null;
		switch (filterBookingEnum){
			case ID_USER:
				entityPage=paymentRepo.findByHistory(startDate,endDate,historyBook.getCari(),historyBook.getUserId(),PaymentEnum.getListApproveBook(),historyBook.getPageable());
				break;
			case SELF_RECEIVER:
				entityPage=paymentRepo.findByHistorySelfReceive(startDate,endDate,historyBook.getCari(),historyBook.getUserId(),PaymentEnum.getListApproveBook(),historyBook.getPageable());
				break;
			case SELF_SENDER:
				entityPage=paymentRepo.findByHistorySelfSender(startDate,endDate,historyBook.getCari(),historyBook.getUserId(),PaymentEnum.getListApproveBook(),historyBook.getPageable());
				break;
			default:
				entityPage=paymentRepo.findByHistory(startDate,endDate,historyBook.getCari(),historyBook.getUserId(),PaymentEnum.getListApproveBook(),historyBook.getPageable());
				break;
		}
		if(entityPage.getTotalElements()==0)throw new NotFoundException("Data Tidak Ditemukan!");
		return
				new PageImpl<>(
						entityPage.getContent().stream().map(this::toBookDataResponse).collect(Collectors.toList()),
						entityPage.getPageable(),
						entityPage.getTotalElements());
	}

	public void saveTrxBook(TPaymentEntity payment) {
		paymentRepo.save(payment);
	}

	public List<TPaymentEntity> getPaymentsNeedPickup(LocalDate dateRequest,Integer pickupTime,Integer areaKotaId){
		return this.paymentRepo.findByPickupTimeIdAndStatus(pickupTime,PaymentEnum.REQUEST.getValue(),dateRequest,areaKotaId,LocalDate.now());
	}

	public String getKodeBook() {
		String bookCode = "A0001";
			TPaymentEntity pay = paymentRepo.findTopByOrderByBookingCodeDesc();
			bookCode = pay.getBookingCode();
			String counter = bookCode.substring(1, 5);
			char h = bookCode.charAt(0);
			if(counter.equals("9999")) {
				int ascii = h;
				ascii++;
				h = (char) ascii;
				bookCode = h + "0001";
			}
			else {
				int count = Integer.valueOf(counter) + 1;
				counter = counter.substring(0, counter.length() - String.valueOf(count).length());
				counter += String.valueOf(count);
			}
			bookCode = h + counter;
		return bookCode;
	}

	public PaymentDto getPaymentByQrCode(String qrCode, PaymentEnum paymentEnum){
		return generatePaymentDto(paymentRepo.findByQrcode(qrCode, paymentEnum.getCode()));
	}
	
	public PaymentDto getPaymentByQrCodeStatus(String qrcode, List<Integer> status,String officeCode) {
		MOfficeEntity mOfficeEntity=officeCodeService.getBranch(officeCode);
		return generatePaymentDto(paymentRepo.findByQrcodeAndStatusAndOrigin(qrcode, status,mOfficeEntity.getCity()));
	}

	public PaymentDto generatePaymentDto(TPaymentEntity paymentEntity){
		if(paymentEntity==null)
			return null;
		return PaymentDto.builder()
				.bookCode(paymentEntity.getBookingCode())
				.paymentEntity(paymentEntity)
				.paymentEnum(PaymentEnum.getPaymentEnum(paymentEntity.getStatus()))
				.build();
	}
	
	public PaymentDto findByqrCodeExt(String qrcode) {
		return generatePaymentDto(paymentRepo.findByQrCodeExtOrQrcodeOrBookingCode(qrcode));
	}
	public PaymentDto findByqrCodeExtOrBookIdOrSTT(String qrcode) {
		return generatePaymentDto(paymentRepo.findByQrcodeOrBookingCode(qrcode));
	}

	public PaymentDto savePayment(PaymentDto paymentDto){
		return generatePaymentDto(save(toPaymentEntity(paymentDto)));
	}
	private TPaymentEntity toPaymentEntity(PaymentDto paymentDto){
		TPaymentEntity entity=paymentDto.getPaymentEntity();
		entity.setStatus(paymentDto.getPaymentEnum().getValue());
		return entity;
	}


	public TPaymentEntity createOldPayment(TPaymentEntity payment){
		TPaymentEntity result=TPaymentEntity.builder().build();
		result.setPrice(payment.getPrice());
		result.setPriceKg(payment.getPriceKg());
		result.setTbooks(payment.getTbooks());
		result.setAmount(payment.getAmount());
		result.setGrossWeight(payment.getGrossWeight());
		result.setVolume(payment.getVolume());
		result.setStatus(payment.getStatus());
		result.setBookingCode(payment.getBookingCode());
		result.setInsurance(payment.getInsurance());
		result.setExtraCharge(payment.getExtraCharge());
		result.setPrice(payment.getPrice());
		result.setAmount(payment.getAmount());
		result.setPriceKg(payment.getPriceKg());
		result.setGrossWeight(payment.getGrossWeight());
		result.setVolume(payment.getVolume());
		result.setStatus(payment.getStatus());
		result.setTbooks(payment.getTbooks());
		return result;
	}

	public BookDataResponse toBookDataResponse(TPaymentEntity paymentEntity,Integer seqId){
		List<TBookEntity> bookEntityList=paymentEntity.getTbooks();
		String dimention=createDimention(bookEntityList);
		PaymentEnum paymentEnum=PaymentEnum.getPaymentEnum(paymentEntity.getStatus());
		StatusPayEnum stsPay = StatusPayEnum.getEnum(paymentEntity.getStatusPay());
		StringBuilder receiverAddress=new StringBuilder();
		receiverAddress.append(paymentEntity.getReceiverAddress());
		receiverAddress.append(areaService.getFullAddressByPostalCode(paymentEntity.getIdPostalCode().getIdPostalCode()));
		String images = PREFIX_PATH_IMAGE_VENDOR + paymentEntity.getProductSwCode().getSwitcherEntity().getImg().substring(paymentEntity.getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
		Double totalWeight = paymentEntity.getGrossWeight().doubleValue() + paymentEntity.getTotalPackKg();
		TPickupDetailEntity pickupDetail = pickupService.getPickupDetail(paymentEntity.getBookingCode());
		String telpRecv = "";
		if(paymentEntity.getStatus() > PaymentEnum.REQUEST.getCode())
			telpRecv = paymentEntity.getReceiverTelp();
		BookDataResponse bookDataResponse= BookDataResponse.builder()
				.amount(paymentEntity.getAmount())
				.userId(paymentEntity.getUserId().getUserId())
				.stt(paymentEntity.getStt())
				.bookingCode(paymentEntity.getBookingCode())
				.pickupAddress(paymentEntity.getPickupAddrId().getAddress())
				.productName(paymentEntity.getProductSwCode().getName())
				.receiverAddress(receiverAddress.toString())
				.receiverName(paymentEntity.getReceiverName())
				.seq(seqId)
				.minWeight(paymentEntity.getMinWeight())
				.shipperName(paymentEntity.getSenderName())
				.senderAddress(paymentEntity.getSenderAddress())
				.statusCode(stsPay.getCodeString())
				.status(paymentEntity.getStatus().toString())
				.statusDesc(paymentEnum.getString())
				.dateTrx(paymentEntity.getTrxDate())
				.timeTrx(paymentEntity.getTrxTime())
				.vendorUrlImage(images)
				.vendorName(paymentEntity.getProductSwCode().getSwitcherEntity().getName())
				.origin(paymentEntity.getOrigin())
				.destination(paymentEntity.getIdPostalCode().getKecamatanEntity().getKecamatan()+","+paymentEntity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.dimension(dimention)
				.qty(paymentEntity.getJumlahLembar())
				.weight(paymentEntity.getGrossWeight())
				.volumeWeight(paymentEntity.getVolume())
				.amount(paymentEntity.getAmount())
				.goodDesc(paymentEntity.getGoodsDesc())
				.pembulatanVolume(paymentEntity.getProductSwCode().getPembulatanVolume())
				.officeName(officeRepo.findAllByOfficeCode(paymentEntity.getOfficeCode())==null?"":officeRepo.findAllByOfficeCode(paymentEntity.getOfficeCode()).getName())
				.isBooking(true)
				.qrcode(paymentEntity.getQrcode())
				.urlResi(Common.getResi(paymentEntity))
				.urlResiVendor(paymentEntity.getResi()==null?"-":paymentEntity.getResi())
				.userPhone(paymentEntity.getUserId().getHp())
				.senderPhone(paymentEntity.getSenderTelp()==null?"-":paymentEntity.getSenderTelp())
				.receiverPhone(paymentEntity.getReceiverTelp()==null?"-":telpRecv)
				.postalCode(paymentEntity.getPickupAddrId().getPostalCode().getPostalCode())
				.paymentOption(paymentEntity.getPaymentOption()==null?"":paymentEntity.getPaymentOption())
				.shipSurcharge(paymentEntity.getShippingSurcharge()==null?0:paymentEntity.getShippingSurcharge().intValue())
				.priceGoods(paymentEntity.getPriceGoods()==null?0:paymentEntity.getPriceGoods().doubleValue())
				.discountCode(paymentEntity.getDiscountCode()==null?"":paymentEntity.getDiscountCode())
				.discountValue(paymentEntity.getDiscountValue()==null?0:paymentEntity.getDiscountValue().doubleValue())
				.totalWeight(totalWeight.intValue())
				.kodeUnik(paymentEntity.getInsufficientFund()==null?0:paymentEntity.getInsufficientFund().intValue())
				.priceKg(paymentEntity.getPriceKg())
				.qrcode(paymentEntity.getQrcode()==null?"":paymentEntity.getQrcode())
				.qrcodeExt(paymentEntity.getQrcodeExt()==null?"":paymentEntity.getQrcodeExt())
				.idPostalCode(paymentEntity.getIdPostalCode().getIdPostalCode())
				.pickup(PickupProperty.builder()
						.pickupDate(paymentEntity.getPickupDate().toString())
						.pickupTime(DateTimeUtil.toString(paymentEntity.getPickupTimeId().getTimeFrom())
								.concat(" - ")
								.concat(DateTimeUtil.toString(paymentEntity.getPickupTimeId().getTimeTo())))
						.pickupDriver((pickupDetail==null)?"-":pickupDetail.getPickupId().getCourierId().getUserId())
						.pickupStatus((pickupDetail==null)?"-":PickupEnum.getEnumByNumber(pickupDetail.getPickupId().getStatus()).name())
						.build())
				.build();
		return bookDataResponse;
	}
	public BookDataResponse getDetailRequestPickup(TPickupOrderRequestEntity entity) {
		String statusLikePayment="";
		PaymentEnum anEnum=PaymentEnum.PENDING;
		RequestPickupEnum requestPickupEnum=RequestPickupEnum.getPaymentEnum(entity.getStatus());
		switch (requestPickupEnum){
			case REQUEST:
				anEnum=PaymentEnum.REQUEST;
				break;
			case IN_COURIER:
				anEnum=PaymentEnum.PICKUP_BY_KURIR;
				break;
			case DRAFT_PICKUP:
				anEnum=PaymentEnum.DRAFT_PICKUP;
				break;
			case ASSIGN_PICKUP:
				anEnum=PaymentEnum.ASSIGN_PICKUP;
				break;
			case IN_WAREHOUSE:
				anEnum=PaymentEnum.RECEIVE_IN_WAREHOUSE;
				break;
			default:
				anEnum=PaymentEnum.ACCEPT_IN_WAREHOUSE;
				break;
		}
		return BookDataResponse.builder()
				.amount(BigDecimal.ZERO)
				.userId(entity.getUserEntity().getUserId())
				.stt("")
				.bookingCode(entity.getPickupOrderId())
				.pickupAddress(entity.getPickupAddressEntity().getAddress())
				.productName("")
				.receiverAddress("")
				.receiverName("")
				.shipperName("")
				.status(anEnum.getCodeString())
				.statusDesc(RequestPickupEnum.getPaymentEnum(entity.getStatus()).toString())
				.dateTrx(entity.getCreateDate().toLocalDate())
				.timeTrx(entity.getCreateDate().format(DateTimeFormatter.ofPattern("HH:mm")))
				.vendorUrlImage("")
				.vendorName("")
				.origin(entity.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.destination("")
				.dimension("")
				.qty(entity.getQty())
				.weight(Long.valueOf("0"))
				.volumeWeight(Long.valueOf("0"))
				.isBooking(false)
				.qrcode("")
				.goodDesc("")
				.userPhone(entity.getUserEntity().getHp())
				.senderPhone("")
				.receiverPhone("")
				.postalCode(entity.getPickupAddressEntity().getPostalCode().getPostalCode())
				.build();
	}
	
	public BookDataResponse toBookDataResponse(TPaymentEntity paymentEntity)
	{
		return toBookDataResponse(paymentEntity,null);
	}
	public BookDataResponse toBookDataResponse(PaymentDto paymentDto)
	{
		return toBookDataResponse(toPaymentEntity(paymentDto));
	}

	public Consumer<? super TPaymentEntity> resetPayment() {
		return p->{
			BigDecimal amt = p.getDiscountValue().add(p.getAmount());
			p.setDiscountCode(null);
			p.setPaymentOption(null);
			if(PaymentEnum.REQUEST==PaymentEnum.getPaymentEnum(p.getStatus())) {
				p.setStatus(PaymentEnum.PENDING.getCode());
			}
			p.setStatusPay(StatusPayEnum.NOT_PAID.getCode());
			p.setTrxServer(new Timestamp(Instant.now().toEpochMilli()));
			p.setAmount(amt);
			p.setIsConfirmTransfer((byte) 1);
			p.setDiscountValue(new BigDecimal("0"));
			p.setCountPawoon(0);
		};
	}
	
	public List<TPaymentEntity> getByStatusAndTime(Integer status,LocalTime time,LocalDate date){
		return paymentRepo.findAllByStatusAndTimeExp(status,date);
	}

	public List<TPaymentEntity> getPayTransfer(List<Integer> status,Byte isconfirm,LocalDate tgl) {
		// TODO Auto-generated method stub
		return paymentRepo.findByStatusAndIsConfirmTransfer(status, isconfirm,tgl);
	}
	
	public List<TPaymentEntity> getPayTransferByStatusAndPickupDate(List<Integer> status,LocalDate tgl) {
		// TODO Auto-generated method stub
		return paymentRepo.findByStatusAndPickupDate(status, tgl);
	}
	
	public List<TPaymentEntity> getPayTransferByStatusAndGroupByAmountUniq(List<Integer> status) {
		// TODO Auto-generated method stub
		return paymentRepo.findByStatusAndGroupByAmountUniq(status);
	}
	
	public List<TPaymentEntity> getByExpiredPayment(Integer status,LocalTime time,LocalDate date){
		return paymentRepo.findAllByStatusAndTimePickup(status, time, date);
	}

	public String createDimention(List<TBookEntity> bookEntities){
		String dimention="";
		for (TBookEntity bookEntity:bookEntities) {
			if(!dimention.equals(""))
				dimention=dimention.concat("\n");
			dimention=dimention.concat(bookEntity.getHeight())
					.concat("x")
					.concat(bookEntity.getLength())
					.concat("x")
					.concat(bookEntity.getWidth());
		}
		return dimention;
	}

	public boolean  updateTransfer(TPaymentEntity pay) {
		// TODO Auto-generated method stub
		List<TPaymentEntity> lspay = paymentRepo.findByNoTiket(pay.getNoTiket());
		lspay.stream().forEach(updateStatus());
		if(lspay.size()<=0) return false;
		paymentRepo.saveAll(lspay);
		return true;
	}

	private Consumer<? super TPaymentEntity> updateStatus() {
		return p -> {
			PaymentEnum oldStatus = PaymentEnum.getPaymentEnum(p.getStatus());
			PaymentEnum newStatus = PaymentEnum.REQUEST;
			if(oldStatus==PaymentEnum.UNPAID_RECEIVE
					|| PaymentEnum.getPaymentEnum(p.getStatus())==PaymentEnum.HOLD_BY_ADMIN) {
				p.setStatus(PaymentEnum.FINISH_INPUT_AND_PAID.getCode());
				newStatus = PaymentEnum.FINISH_INPUT_AND_PAID;
			}else {
				p.setStatus(PaymentEnum.REQUEST.getCode());
			}
			if(p.getUserId().getUserCategory().getSeqid().equals(USER_CATEGORY_COUNTER)) {
				p.setStatus(PaymentEnum.RECEIVE_IN_COUNTER.getCode());
				newStatus = PaymentEnum.RECEIVE_IN_COUNTER;
			}
			this.createHistory(p, oldStatus, newStatus);
			p.setIsConfirmTransfer((byte) StatusPayEnum.PAID.getCode());
			p.setStatusPay(StatusPayEnum.PAID.getCode());
		};
	}

	public List<TPaymentEntity> getTPaymentByPickupDate(String userId,Integer idPickupTime,Integer areaDetailId,Integer areaKotaId,LocalDate pickupDate){
		return paymentRepo.findAllByUserAndStatusAndTimePickup(userId, PaymentEnum.REQUEST.getCode(), idPickupTime, areaDetailId, areaKotaId, pickupDate);
	}
	public List<Integer> getTPickupAdrressByPickupDate(String userId, Integer idPickupTime, Integer areaDetailId, Integer areaKotaId, LocalDate pickupDate){
		return paymentRepo.findPickupAddrIdAllByUserAndStatusAndTimePickup(userId, PaymentEnum.REQUEST.getCode(), idPickupTime, areaDetailId, areaKotaId, pickupDate);
	}
	public List<TPaymentEntity> getTPaymentByPickupAddressRequest(Integer pickupAddressId){
		return  paymentRepo.findByPickupAddrIdPickupAddrIdAndStatus(pickupAddressId,PaymentEnum.REQUEST.getCode());
	}
	
	public List<TPaymentEntity> getTPaymentByPickupAddressRequest(Integer pickupAddressId,String userid,Integer areaDetailId,Integer areaKotaId,Integer pickupTimeId,LocalDate pickupDate){
		return  paymentRepo.findByPickupAddrIdPickupAddrIdAndStatusAndUserIdUserIdAndIdPostalCodeKecamatanEntityAreaDetailIdAndIdPostalCodeKecamatanEntityKotaEntityAreaKotaIdAndPickupTimeIdIdPickupTimeAndPickupDate(pickupAddressId,
				PaymentEnum.REQUEST.getCode(),userid,areaDetailId,areaKotaId,pickupTimeId,pickupDate);
	}
	public List<TPaymentEntity> getTPaymentByPickupAddressRequest(List<TPickupAddressEntity> pickupAddressId){
		return  paymentRepo.findByPickupAddrIdInAndStatus(pickupAddressId,PaymentEnum.REQUEST.getCode());
	}
    
    public List<TPaymentEntity> findByOfficeCode(PaymentEnum paymentEnum,PaymentEnum pEnum){
    	return paymentRepo.findAllByOfficeCode(paymentEnum.getCode(),pEnum.getCode());
    }
    
    public List<TPaymentEntity> findByNoTiket(String tiketNo){
    	return paymentRepo.findAllByNoTiket(tiketNo);
    }
    
    public PaymentDto findByBookingCodeAndStatusRequest(String bookingCode, List<Integer> status) {
    	return generatePaymentDto(paymentRepo.findByQrcodeAndStatus(bookingCode, status));
    }
    
    public void createHistory(TPaymentEntity p,PaymentEnum statusOld, PaymentEnum statusNew) {
		TPaymentEntity oldpay = p;
		oldpay.setStatus(statusOld.getCode());
		p.setStatus(statusNew.getCode());
		histService.createHistory(oldpay, p, p.getUserId().getUserId());
	}
    
    public void createHistory(TPaymentEntity p,PaymentEnum statusOld, PaymentEnum statusNew,String reason) {
		TPaymentEntity oldpay = p;
		oldpay.setStatus(statusOld.getCode());
		p.setStatus(statusNew.getCode());
		histService.createHistory(oldpay, p, p.getUserId().getUserId(),reason);
	}
    public Page<TPaymentEntity> getListBookingByLeadTimeStatus(Integer productCode,Integer vendorCode,LocalDate start,LocalDate end,String status,String userId,String areaId,Pageable pageable){
    	return paymentRepo.getListBookingByLeadTimeStatus(start, end, productCode, vendorCode, status,userId,areaId,pageable);
    }
    
    public Integer getTotalByuserCategory(MUserCategoryEntity userCategory, LocalDate trxDate) {
    	return paymentRepo.countByStatusAndUserCategoryAndTrxDate(userCategory, trxDate);
    }
    
    public Integer getTotatTrxMonthByUserCategory(MUserCategoryEntity userCategoryEntity, LocalDate trxDate) {
    	return paymentRepo.countByStatusAndUserCategoryAndMonth(trxDate.getMonthValue(), trxDate.getYear(), userCategoryEntity);
    }
}
