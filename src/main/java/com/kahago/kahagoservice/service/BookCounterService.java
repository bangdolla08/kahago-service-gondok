package com.kahago.kahagoservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MAreaKotaEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import com.kahago.kahagoservice.enummodel.SaveTrxEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.model.request.BookRequest;
import com.kahago.kahagoservice.model.request.CompleteBookReq;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.RespUncomplete;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MAreaKotaRepo;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;


@Service
public class BookCounterService {
	private List<RespUncomplete> results;
	@Autowired
	private BookService bookService;
	@Autowired
	private MPostalCodeRepo postalCode;
	@Autowired
	private MAreaKotaRepo kotaRepo;
	@Autowired
	private MOfficeRepo officeRepo;
	@Autowired
	private MAreaRepo areaRepo;
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private DepositBookService depositBookService;

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public SaveResponse doBookCounter(List<CompleteBookReq> request,String userlogins) {
		this.results = new ArrayList<>();
		results = request.stream().map(p->getBook(p, userlogins)).collect(Collectors.toList());
		return SaveResponse.builder()
				.results(results)
				.saveStatus(1)
				.build();
	}
	
	private RespUncomplete getBook(CompleteBookReq req,String userlogin) {
		MPostalCodeEntity postal = postalCode.getOne(req.getIdPostalCode());
		MAreaKotaEntity kota = null;
		MOfficeEntity office = null;
		boolean isCounter = false;
		if(req.getOriginId()==null) {
			office = officeRepo.findAllByOfficeCode(req.getOfficeCode());
			MAreaEntity area = areaRepo.getOne(office.getRegionCode());
			kota = area.getKotaEntity();
			isCounter= (office.getUnitType().equals("2"))?true:false;
		}else {
			kota = kotaRepo.getOne(req.getOriginId());
		}
		if(!isCounter) {
			boolean	isQrcodeExt = payRepo.existByQRCodeExt(req.getQrCodeExt());
			if(isQrcodeExt) {
				throw new InternalServerException("Qrcode External Duplikat");
			}
			if(depositBookService.checkQrCodeExt(req.getQrCodeExt())) {
				throw new InternalServerException("QrCode : "+req.getQrCodeExt() +" sudah digunakan !");
			}
		}
		
		BookRequest request = BookRequest.builder()
				.comodity(req.getGoodsId())
				.destination(postal.getKecamatanEntity().getKotaEntity().getName())
				.detailBooking(req.getDetailBooking())
				.goodsDescription(req.getDescription())
				.idPickupTime(1)
				.minWeight(1)
				.note("")
				.origin(kota.getName())
				.payType("1")
				.pickupDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.pickupId("0")
				.ppn("")
				.priceGoods(req.getGoodsPrice())
				.productCode(req.getProductCode().toString())
				.quantity(String.valueOf(req.getDetailBooking().size()))
				.receiverAddress(req.getReceiverAddress())
				.receiverEmail(req.getReceiverEmail())
				.receiverName(req.getReceiverName())
				.receiverPostalCode(req.getIdPostalCode().toString())
				.receiverSave("0")
				.receiverTelp("0"+req.getReceiverPhoneNumber())
				.senderAddress(req.getSenderAddress())
				.senderEmail(req.getSenderEmail())
				.senderName(req.getSenderName())
				.senderSave("0")
				.senderTelp("0"+req.getSenderPhoneNumber())
				.serviceType("0")
				.totalGrossWeight(String.valueOf(Math.ceil(req.getDetailBooking().stream().mapToDouble(p->p.getGrossWeight()).sum())))
				.totalInsurance((req.getIsAsuransi())?"1":"0")
				.totalPackingPrice((req.getIsPackingKayu())?"1":"0")
				.totalPackKg((req.getIsPackingKayu())?"1":"0")
				.totalPrice("0")
				.totalSurcharge("0")
				.totalVolume(String.valueOf(Math.ceil(req.getDetailBooking().stream().mapToDouble(p->p.getVolume()).sum())))
				.userId(userlogin)
				.isCounter(true)
				.officeCode(req.getOfficeCode())
				.qrcodeExt((isCounter)?"":replaceRegexQrcodeExt(req.getQrCodeExt()))
				.build();
		BookResponse resp = bookService.booking(request, "WEB");
		return RespUncomplete.builder()
				.bookingCode(resp.getBookingCode())
				.qrcode(req.getQrCode())
				.build();
	}
	
	public String replaceRegexQrcodeExt(String qrcodeExt) {
		String [] qrCodeExttoArray = qrcodeExt.split("");
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
		for(String str : qrCodeExttoArray) {
			Matcher matcher = pattern.matcher(str);
			if(!matcher.matches()) {
				qrcodeExt = qrcodeExt.replace(str, "");
			}
		}
		return qrcodeExt;
	}

}
