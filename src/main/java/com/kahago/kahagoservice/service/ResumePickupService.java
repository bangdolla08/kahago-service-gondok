package com.kahago.kahagoservice.service;
/**
 * @author Ibnu Wasis
 */
import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.ResumePickupReq;
import com.kahago.kahagoservice.model.response.DimensiGoods;
import com.kahago.kahagoservice.model.response.ResumePickupResponse;
import com.kahago.kahagoservice.repository.TBookRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;



@Service
public class ResumePickupService {
	@Autowired
	private TPickupDetailRepo tPickupDetailRepo;
	@Autowired
	private TBookRepo tBookRepo;
	@Value("${endpoint.report.manifest}")
	private String endpointReport;
	@Autowired
	private TPickupOrderRequestRepo pickupRequestRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo pickupReqDtlRepo;
	
	public static final Logger logger = LoggerFactory.getLogger(ResumePickupService.class);
	int seq = 0;
	public List<ResumePickupResponse> getDataResume(ResumePickupReq request){
		List<ResumePickupResponse> lresume = new ArrayList<ResumePickupResponse>();
		List<TPickupDetailEntity> ldetail = new ArrayList<>();
		LocalDate start = null;
		LocalDate end = null;
		try {
			if((request.getStartDate()!=null && !request.getStartDate().isEmpty()) &&
					(request.getEndDate()!=null && !request.getEndDate().isEmpty())) {
				 start = DateTimeUtil.getDateFrom(request.getStartDate(), "dd/MM/yyyy");
				 end = DateTimeUtil.getDateFrom(request.getEndDate(), "dd/MM/yyyy");
			}
		}catch (ParseException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new NotFoundException("Error Parsing Date!");
		}
		
		if((request.getNoManifest() != null) || (request.getCustomerName() != null)) {
			if(request.getNoManifest() != null && request.getCustomerName() == null) {
				ldetail = tPickupDetailRepo.findByNoManifest(request.getNoManifest().toLowerCase(), request.getUserId());
			}else if(request.getCustomerName() != null && request.getNoManifest() == null ) {
				ldetail = tPickupDetailRepo.findByNoManifestByName(request.getUserId());
				getResumeByName(ldetail, request.getCustomerName().toLowerCase());
			}else if(request.getNoManifest() != null && request.getCustomerName() != null) {
				ldetail = tPickupDetailRepo.findByNoManifest(request.getNoManifest().toLowerCase(), request.getUserId());
				getResumeByName(ldetail, request.getCustomerName().toLowerCase());
			}
			seq=0;
			if(start!=null && end!=null) {
				if(ldetail.size()==0)throw new NotFoundException("Data Tidak Ditemukan!");
				for(TPickupDetailEntity td :ldetail) {
					seq=seq+1;
					if(td.getPickupId().getPickupDate().compareTo(start) >= 0 && td.getPickupId().getPickupDate().compareTo(end) <= 0) {
						if(td.getBookId()!=null) {
							lresume.add(toDto(td, seq));
						}else {
							lresume.addAll(getPickupRequestDtl(td));
							seq=seq-1;
						}
						
					}
				}
			}else {
				if(ldetail.size()==0)throw new NotFoundException("Data Tidak Ditemukan!");
				for(TPickupDetailEntity td :ldetail) {
					seq=seq+1;
					if(td.getBookId()!=null) {
						lresume.add(toDto(td, seq));
					}else {
						lresume.addAll(getPickupRequestDtl(td));
						seq=seq-1;
					}
				}
			}
		}else {
			seq=0;
			if(start!=null && end!=null) {
				ldetail = tPickupDetailRepo.findByPickupDate(start, end, request.getUserId());
				if(ldetail.size()==0)throw new NotFoundException("Data Tidak Ditemukan!");
				for(TPickupDetailEntity td :ldetail) {
					seq=seq+1;
					if(td.getBookId()!=null) {
						lresume.add(toDto(td, seq));
					}else {
						lresume.addAll(getPickupRequestDtl(td));
						seq=seq-1;
					}
				}
			}
		}
		return lresume;
	}
	
	public ResumePickupResponse getDetail(String bookingCode, String noManifest) {
		TPickupDetailEntity detail = tPickupDetailRepo.findByBookIdAndCode(bookingCode, noManifest);
		if(detail.getBookId()==null) {
			return getDetailPickupReq(detail, 1);
		}
		return toDto(detail, 1);
		
	}
	
	private ResumePickupResponse toDto(TPickupDetailEntity entity,Integer seq) {
		String images = "-";
		if(entity.getBookId()!=null) {
			images = PREFIX_PATH_IMAGE_VENDOR + entity.getBookId().getProductSwCode().getSwitcherEntity().getImg().substring(entity.getBookId().getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
		}
		String imagePickup="";
		if(entity.getPathPic()!=null) {
			imagePickup = PREFIX_PATH_IMAGE_PICKUP + entity.getPathPic().substring(entity.getPathPic().lastIndexOf("/")+1);
		}
		
		String uri = endpointReport;
		uri = uri.replaceAll("#code", entity.getPickupId().getCode());
		return ResumePickupResponse.builder()
				.seq(seq)
				.customerName(entity.getBookId().getUserId().getName())
				.address(entity.getBookId().getPickupAddrId().getAddress())
				.kelurahan(entity.getBookId().getPickupAddrId().getPostalCode().getKelurahan())
				.kecamatan(entity.getBookId().getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan())
				.kota(entity.getBookId().getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.addressNote(entity.getBookId().getPickupAddrId().getDescription()==null?"":entity.getBookId().getPickupAddrId().getDescription())
				.customerId(entity.getBookId().getUserId().getUserId())
				.postalCode(entity.getBookId().getPickupAddrId().getPostalCode().getPostalCode())
				.bookingCode(entity.getBookId().getBookingCode())
				.qty(String.valueOf(entity.getBookId().getJumlahLembar()))
				.weight(String.valueOf(entity.getBookId().getGrossWeight()))
				.volume(String.valueOf(entity.getBookId().getVolume()))
				.isInsurance(entity.getBookId().getInsurance().compareTo(BigDecimal.ZERO)<=0?"0":"1")
				.noManifest(entity.getPickupId().getCode())
				.statusDesc(PaymentEnum.getPaymentEnum(entity.getBookId().getStatus()).toString())
				.qrcode(entity.getBookId().getQrcodeExt()==null?"":entity.getBookId().getQrcodeExt())
				.vendor(entity.getBookId().getProductSwCode().getSwitcherEntity().getDisplayName())
				.productDisplayName(entity.getBookId().getProductSwCode().getDisplayName())
				.courierId(entity.getPickupId().getCourierId().getUserId())
				.imagesVendor(images)
				.lbooks(getDimensi(entity.getBookId().getBookingCode()))
				.isPacking(entity.getBookId().getExtraCharge().compareTo(BigDecimal.ZERO)<=0?"0":"1")
				.customerTelp(entity.getBookId().getUserId().getHp())
				.linkManifest(uri)
				.pathImage(imagePickup)
				.pickupDate(entity.getBookId().getPickupDate())
				.pickupTime(entity.getBookId().getPickupTime())
				.status(entity.getBookId().getStatus())
				.statusPickup(entity.getStatus())
				.productSwCode(entity.getBookId().getProductSwCode().getProductSwCode())
				.statusPickupDesc(PickupEnum.getEnumByNumber(entity.getStatus()).toString())
				.statusManifest(entity.getPickupId().getStatus())
				.isBooking(true)
				.build();
				
	}
	private ResumePickupResponse getDetailPickupReq(TPickupDetailEntity entity,Integer seq) {
		String uri = endpointReport;
		uri = uri.replaceAll("#code", entity.getPickupId().getCode());
		String imagePickup="";
		if(entity.getPathPic()!=null) {
			imagePickup = PREFIX_PATH_IMAGE_PICKUP + entity.getPathPic().substring(entity.getPathPic().lastIndexOf("/")+1);
		}
		return ResumePickupResponse.builder()
				.seq(seq)
				.customerName(entity.getPickupOrderRequestEntity().getUserEntity().getName())
				.address(entity.getPickupOrderRequestEntity().getPickupAddressEntity().getAddress())
				.kelurahan(entity.getPickupOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKelurahan())
				.kecamatan(entity.getPickupOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKecamatan())
				.kota(entity.getPickupOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.addressNote(entity.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription()==null?"":entity.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription())
				.customerId(entity.getPickupOrderRequestEntity().getUserEntity().getUserId())
				.postalCode(entity.getPickupOrderRequestEntity().getPickupAddressEntity().getPostalCode().getPostalCode())
				.bookingCode(entity.getPickupOrderRequestEntity().getPickupOrderId())
				.qty(String.valueOf(entity.getPickupOrderRequestEntity().getQty()))
				.weight("-")
				.volume("-")
				.isInsurance("0")
				.noManifest(entity.getPickupId().getCode())
				.statusDesc(RequestPickupEnum.getPaymentEnum(entity.getPickupOrderRequestEntity().getStatus()).toString())
				.qrcode("")
				.vendor("-")
				.productDisplayName("-")
				.courierId(entity.getPickupId().getCourierId().getUserId())
				.imagesVendor("-")
				.lbooks(getDimensi(entity.getPickupOrderRequestEntity().getPickupOrderId()))
				.isPacking("0")
				.customerTelp(entity.getPickupOrderRequestEntity().getUserEntity().getHp())
				.linkManifest(uri)
				.pathImage(imagePickup)
				.pickupDate(entity.getPickupOrderRequestEntity().getOrderDate())
				.pickupTime(entity.getPickupOrderRequestEntity().getPickupTimeEntity().getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+entity.getPickupOrderRequestEntity().getPickupTimeEntity().getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
				.status(entity.getPickupOrderRequestEntity().getStatus())
				.statusPickup(entity.getStatus())
				.productSwCode(Long.valueOf("0"))
				.statusPickupDesc(PickupEnum.getEnumByNumber(entity.getStatus()).toString())
				.statusManifest(entity.getPickupId().getStatus())
				.isBooking(false)
				.build();
	}
	private List<ResumePickupResponse> getPickupRequestDtl(TPickupDetailEntity entity){
		List<ResumePickupResponse> result = new ArrayList<>();
		List<TPickupOrderRequestDetailEntity> lPickupReq = pickupReqDtlRepo.findAllByOrderRequestEntity(entity.getPickupOrderRequestEntity());
		String uri = endpointReport;
		String images = "-";		
		uri = uri.replaceAll("#code", entity.getPickupId().getCode());
		String imagePickup ="-";
		if(entity.getPathPic()!=null) {
			imagePickup = PREFIX_PATH_IMAGE_PICKUP + entity.getPathPic().substring(entity.getPathPic().lastIndexOf("/")+1);
		}
		for(TPickupOrderRequestDetailEntity pdt : lPickupReq) {
			if(pdt.getProductSwitcherEntity()!=null) {
				images = PREFIX_PATH_IMAGE_VENDOR + pdt.getProductSwitcherEntity().getSwitcherEntity().getImg().substring(pdt.getProductSwitcherEntity().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
			}
			ResumePickupResponse  rps = ResumePickupResponse.builder()
					.seq(seq)
					.customerName(pdt.getOrderRequestEntity().getUserEntity().getName())
					.address(pdt.getOrderRequestEntity().getPickupAddressEntity().getAddress())
					.kelurahan(pdt.getOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKelurahan())
					.kecamatan(pdt.getOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKecamatan())
					.kota(pdt.getOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
					.addressNote(pdt.getOrderRequestEntity().getPickupAddressEntity().getDescription()==null?"":entity.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription())
					.customerId(pdt.getOrderRequestEntity().getUserEntity().getUserId())
					.postalCode(pdt.getOrderRequestEntity().getPickupAddressEntity().getPostalCode().getPostalCode())
					.bookingCode(pdt.getOrderRequestEntity().getPickupOrderId())
					.qty(String.valueOf(pdt.getQty()))
					.weight(pdt.getWeight()==null?"-":String.valueOf(pdt.getWeight()))
					.volume("-")
					.isInsurance("0")
					.noManifest(entity.getPickupId().getCode())
					.statusDesc(RequestPickupEnum.getPaymentEnum(pdt.getStatus()).toString())
					.qrcode(pdt.getQrcodeExt()==null?"-":pdt.getQrcodeExt())
					.vendor(pdt.getProductSwitcherEntity()==null?"":pdt.getProductSwitcherEntity().getSwitcherEntity().getName())
					.productDisplayName(pdt.getProductSwitcherEntity()==null?"-":pdt.getProductSwitcherEntity().getName())
					.courierId(entity.getPickupId().getCourierId().getUserId())
					.imagesVendor(images)
					.lbooks(getDimensi(pdt.getOrderRequestEntity().getPickupOrderId()))
					.isPacking("0")
					.customerTelp(pdt.getOrderRequestEntity().getUserEntity().getHp())
					.linkManifest(uri)
					.pathImage(imagePickup)
					.pickupDate(pdt.getOrderRequestEntity().getOrderDate())
					.pickupTime(pdt.getOrderRequestEntity().getPickupTimeEntity().getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+entity.getPickupOrderRequestEntity().getPickupTimeEntity().getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.status(pdt.getStatus())
					.statusPickup(entity.getStatus())
					.productSwCode(Long.valueOf("0"))
					.statusPickupDesc(PickupEnum.getEnumByNumber(entity.getStatus()).toString())
					.statusManifest(entity.getPickupId().getStatus())
					.isBooking(false)
					.build();
			seq=seq+1;
			result.add(rps);			
		}
		return result;
	}
	
	private void getResumeByName(List<TPickupDetailEntity> ldetail,String customerName) {
		List<TPickupDetailEntity> result = new ArrayList<>();
		for(TPickupDetailEntity dt : ldetail) {
			if(dt.getBookId() != null) {
				if(dt.getBookId().getUserId().getName().toLowerCase().contains(customerName)) {
					result.add(dt);
				}
			}else {
				if(dt.getPickupOrderRequestEntity().getUserEntity().getName().toLowerCase().contains(customerName)) {
					result.add(dt);
				}
			}
		}
		ldetail.clear();
		ldetail.addAll(result);
	}
	private List<DimensiGoods> getDimensi(String bookingCode){
		List<DimensiGoods> ldimensi = new ArrayList<>();
		List<TBookEntity> lBooks = tBookRepo.findByBookingCode(bookingCode);
		if(lBooks.size()==0) {
			DimensiGoods dg = new DimensiGoods();
			dg.setHeight("-");
			dg.setLength("-");
			dg.setWidth("-");
			ldimensi.add(dg);
			return ldimensi;
		}
		for(TBookEntity te : tBookRepo.findByBookingCode(bookingCode)) {
			DimensiGoods dg = new DimensiGoods();
			dg.setHeight(te.getHeight());
			dg.setLength(te.getLength());
			dg.setWidth(te.getWidth());
			ldimensi.add(dg);
			}
		return ldimensi;
	}
	
}
