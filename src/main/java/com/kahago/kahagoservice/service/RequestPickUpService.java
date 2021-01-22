package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.RequestPickupReqDto;
import com.kahago.kahagoservice.model.request.DetailBooks;
import com.kahago.kahagoservice.model.request.DetailRequestPickUpReq;
import com.kahago.kahagoservice.model.request.ListBookingCompleteReq;
import com.kahago.kahagoservice.model.request.PickupListRequest;
import com.kahago.kahagoservice.model.request.PickupOrderListRequest;
import com.kahago.kahagoservice.model.request.PriceRequest;
import com.kahago.kahagoservice.model.request.RequestPickUpReq;
import com.kahago.kahagoservice.model.response.ListBookingCompleteResponse;
import com.kahago.kahagoservice.model.response.PickupOrderDetail;
import com.kahago.kahagoservice.model.response.PriceDetail;
import com.kahago.kahagoservice.model.response.PriceResponse;
import com.kahago.kahagoservice.model.response.RequestPickUpResp;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MAreaDetailRepo;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MPickupTimeRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MUserPriorityRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveDetailRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.controller
 */
@Service
public class RequestPickUpService {
    @Autowired
    private TPickupOrderRequestRepo pickupOrderRequestRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo orderRequestDetailRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private PickupAddressService pickupAddressService;
    @Autowired
    private MPickupTimeRepo pickupTimeRepo;
    @Autowired
    private MProductSwitcherRepo productSwitcherRepo;
    @Autowired
    private WarehouseVerificationService warehouseVerification;
    @Autowired
    private MUserRepo mUserRepo;
    @Autowired
    private MAreaRepo mAreaRepo;
    @Autowired
    private MAreaDetailRepo areaDetailRepo;
    @Autowired
    private MUserPriorityRepo mUserPriorityRepo;
    @Autowired
    private PriceService priceService;
    @Autowired
    private TCreditRepo tCreditRepo;
    @Autowired
    private BookService bookService;
    @Autowired
    private MOfficeRepo officeRepo;
    @Autowired
    private TMutasiRepo tMutasiRepo;
    @Autowired
    private TPickupDetailRepo tPickupDetailRepo;
    @Autowired
    private TWarehouseReceiveDetailRepo whReceiveDetailRepo;
    @Autowired
    private MAreaRepo areaRepo;
    @Autowired
    private HistoryTransactionService historyTransactionService;
	@Autowired
	private TPickupDetailRepo pickupDetailRepo;
    private static final String SBY = "SUB";
	private static final String SDA = "SDA";
	private static final String FLAG = "0";
	private static final Integer ISPAY= 1 ; 

    public TPickupOrderRequestEntity savePickupOrderRequestEntity(TPickupOrderRequestEntity pickupOrderRequestEntity){
        return this.pickupOrderRequestRepo.save(pickupOrderRequestEntity);
    }

    public TPickupOrderRequestEntity getPickupOrderRequestEntity(String requestId){
        return this.pickupOrderRequestRepo.findByPickupOrderId(requestId);
    }
    public void saveAll(List<TPickupOrderRequestDetailEntity> lpickupReq) {
    	orderRequestDetailRepo.saveAll(lpickupReq);
    }
    public List<TPickupOrderRequestEntity> getPickupOrderList(String userId, LocalDate date, Integer pickupTimeId, RequestPickupEnum pickupEnum, Integer areaKotaId){
        return this.pickupOrderRequestRepo.findByOrderDateAndPickupTimeEntityIdPickupTime(date,pickupTimeId,pickupEnum.getValue(),areaKotaId,LocalDate.now());
    }
    
    public List<TPickupOrderRequestEntity> getListPickupOrder(String userId,Integer timePickupId,LocalDate pickupDate,Integer areaDetailId,Integer areaKotaId){
    	return pickupOrderRequestRepo.findAllByUserIdAndPickupAddress(userId, RequestPickupEnum.REQUEST.getValue(), timePickupId, areaDetailId, areaKotaId, pickupDate);
    }
	public List<TPickupOrderRequestEntity> getLisPickupOrderByAddress(Integer addressEntity){
		return pickupOrderRequestRepo.findByPickupAddressEntityPickupAddrIdAndStatus(addressEntity,RequestPickupEnum.REQUEST.getValue());
	}
	public List<TPickupOrderRequestEntity> getLisPickupOrderByAddress(Integer addressEntity,String userid,Integer pickupTimeId,LocalDate pickupDate){
		return pickupOrderRequestRepo.findByStatusAndPickupAddressEntityPickupAddrIdAndUserEntityUserIdAndPickupTimeEntityIdPickupTimeAndOrderDate(RequestPickupEnum.REQUEST.getValue(), addressEntity, userid, pickupTimeId, pickupDate);
	}
	public List<TPickupOrderRequestEntity> getLisPickupOrderByAddress(Integer addressEntity,TPickupEntity pickup,List<Integer> status){
		return pickupOrderRequestRepo.findByStatusAndPickupAddressEntityPickupAddrIdAndPickupEntityPickupId(status,addressEntity, pickup);
	}
	public List<TPickupOrderRequestEntity> getLisPickupOrderByAddress(List<TPickupAddressEntity> addressEntity){
		return pickupOrderRequestRepo.findByPickupAddressEntityInAndStatus(addressEntity,RequestPickupEnum.REQUEST.getValue());
	}
	public List<Integer> getListPickupOrderByAddress(String userId,Integer timePickupId,LocalDate pickupDate,Integer areaDetailId,Integer areaKotaId,List<TPickupAddressEntity> addressEntityList){
		return pickupOrderRequestRepo.findTPickupAddressEntityAllByUserIdAndPickupAddress(userId, RequestPickupEnum.REQUEST.getValue(), timePickupId, areaDetailId, areaKotaId, pickupDate);
	}
    public RequestPickupReqDto validateInput(RequestPickUpReq requestPickUpReq){
        RequestPickupReqDto pickupReqDto=new RequestPickupReqDto();
        MUserEntity user = mUserRepo.getMUserEntitiesBy(requestPickUpReq.getUserId());
        pickupReqDto.setUserEntity(userService.get(requestPickUpReq.getUserId()));
        pickupReqDto.setPickupAddressEntity(pickupAddressService.getPickupAddressEntity(requestPickUpReq.getPickupAddressID()));
        pickupReqDto.setPickupTimeEntity(pickupTimeRepo.findByIdPickupTime(requestPickUpReq.getPickupTimeId()));
        MUserPriorityEntity userPriority = mUserPriorityRepo.findByUserCategory(user.getUserCategory().getSeqid());
        TPickupOrderRequestEntity pickupReq = pickupOrderRequestRepo.findByUserEntityAndPickupTimeEntityAndStatusAndOrderDateAndPickupAddressEntity(user, pickupReqDto.getPickupTimeEntity(), 
        		RequestPickupEnum.REQUEST.getValue(),LocalDate.now(),pickupReqDto.getPickupAddressEntity());
        if(pickupReqDto.getUserEntity()==null){
            throw new NotFoundException("Data user tidak di temukan");
        }else if (pickupReqDto.getPickupAddressEntity()==null){
            throw new NotFoundException("Data Pickup Address Tidak ditemukan");
        }else if(pickupReqDto.getPickupTimeEntity()==null){
            throw new NotFoundException("Data pickup time tidak ditemukan");
        }else if (pickupReq != null) {
        	throw new NotFoundException("Tidak Boleh Request Pickup di jam pickup yang sama !");
        }else if(userPriority != null && requestPickUpReq.getQtyItem()!=null ) {
        	if(requestPickUpReq.getQtyItem() < userPriority.getMinKiriman() && requestPickUpReq.getQtyItem() > 0) {
        		 throw new NotFoundException("Belum mencapai minimal kiriman : "+userPriority.getMinKiriman());
        	}
        }
        return pickupReqDto;
    }
   /* private String createOrderNumber(String tiketStandart, String pickupDate) throws ParseException{
        Date date= Calendar.getInstance().getTime();
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormater=new SimpleDateFormat("yyMMdd");
        if(pickupDate != null) {
        	date = dateFormat.parse(pickupDate);
        }
        String tiket="0";
        
        String a="A";
        String count=tiketStandart+a+"0000";
        String depositCount=pickupOrderRequestRepo.getCodeCount(a);
        if(depositCount!=null)
            count = depositCount;
        count = Common.getCounter(count,10,14);
        tiket = tiketStandart+a+count;
        return tiket;
    }*/
    
    public String createOrderNumb() {
    	TPickupOrderRequestEntity pickup = pickupOrderRequestRepo.findTopByOrderByPickupOrderIdDesc();
    	String orderNumb = "";
    	if(pickup == null) {
    		orderNumb = "COBA01";
    	}else {
    		orderNumb = pickup.getPickupOrderId();
    	} 
    	String counter = orderNumb.substring(2, 6);
    	char x = orderNumb.charAt(0);
    	char h = orderNumb.charAt(1);
    	if(x != 'P') {
    		orderNumb = "PA0001";
    	}else {
    		if(counter.equals("9999")) {
                int ascii = h;
                ascii++;
                h = (char) ascii;
                orderNumb = "P" + h + "0001";
            } else {
                int count = Integer.valueOf(counter) + 1;
                counter = counter.substring(0, counter.length() - String.valueOf(count).length());
                counter += String.valueOf(count);
                orderNumb = "P"+ h + counter;
            }
    	}
    	return orderNumb;
    }
    public RequestPickUpResp setRequestBook(RequestPickUpReq requestPickUpReq,RequestPickupReqDto requestPickupReqDto,String userLogin) throws ParseException {
        TPickupOrderRequestEntity orderRequestEntity=TPickupOrderRequestEntity.builder()
                .pickupOrderId(createOrderNumb())
                .userEntity(requestPickupReqDto.getUserEntity())
                .pickupAddressEntity(requestPickupReqDto.getPickupAddressEntity())
                .createBy(userLogin)
                .createDate(LocalDateTime.now())
                .orderDate(DateTimeUtil.getDateFrom(requestPickUpReq.getPickupDate(),"yyyy-MM-dd"))
                .pickupTimeEntity(requestPickupReqDto.getPickupTimeEntity())
                .qty(requestPickUpReq.getQtyItem()==null?0:requestPickUpReq.getQtyItem())
                .status(RequestPickupEnum.REQUEST.getValue())
                .build();
        pickupOrderRequestRepo.save(orderRequestEntity);
        String origin = "";
        if(requestPickupReqDto.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName().equals("Surabaya")) {
        	origin = SBY;
        }else {
        	origin= SDA;
        }
        PriceRequest pricereq = new PriceRequest();
		PriceDetail pDetail = null;
		BigDecimal amount = BigDecimal.ZERO;
        List<TPickupOrderRequestDetailEntity> detailEntities=new ArrayList<>();
        if(requestPickUpReq.getDetail()!=null) {
            for (DetailRequestPickUpReq detailRequestPickUpReq : requestPickUpReq.getDetail()) {
            	if(detailRequestPickUpReq != null && detailRequestPickUpReq.getWeight() != null) {
            		pricereq.setOrigin(origin);
                	pricereq.setUserId(requestPickUpReq.getUserId());
                	pricereq.setDestination(detailRequestPickUpReq.getAreaDetailId());
                	pricereq.setWeight(detailRequestPickUpReq.getWeight().intValue());
                	PriceResponse priceresp = priceService.findPrice(pricereq);
                	for(PriceDetail pr : priceresp.getPrices()) {
            			if(pr.getProductCode().equals(detailRequestPickUpReq.getProductSwitcherCode().toString())) {
            				pDetail = pr;
            				break;
            			}
            		}
                	if(pDetail == null) {
            			throw new NotFoundException("Tarif Tidak Ditemukan!");
            		}
                	amount = pDetail.getPrice().multiply(BigDecimal.valueOf(detailRequestPickUpReq.getWeight()));
            	}            	
                MProductSwitcherEntity productSwitcherEntity = productSwitcherRepo.getOne(detailRequestPickUpReq.getProductSwitcherCode());
                MAreaDetailEntity areaid = areaDetailRepo.getOne(detailRequestPickUpReq.getAreaDetailId());
                if (productSwitcherEntity == null) {
                    throw new NotFoundException("Product Swithcet tidak ditemukan");
                }
                if(areaid == null) {
                	throw new NotFoundException("Area tidak Ditemukan !");
                }
                TPickupOrderRequestDetailEntity orderRequestDetailEntity =
                            TPickupOrderRequestDetailEntity.builder().
                                    orderRequestEntity(orderRequestEntity).
                                    namaPenerima(detailRequestPickUpReq.getReceiverName()).
                                    areaId(areaid).
                                    weight(detailRequestPickUpReq.getWeight()).
                                    qty(detailRequestPickUpReq.getQtyItem()).
                                    productSwitcherEntity(productSwitcherEntity).
                                    createDate(LocalDateTime.now())
                                    .createBy(requestPickUpReq.getUserId())
                                    .qrCode(Common.gerQrCode())
                                    .isPay(detailRequestPickUpReq.getIsPay())
                                    .amount(amount)
                                    .status(RequestPickupEnum.REQUEST.getValue())
                                    .build();
                    detailEntities.add(orderRequestDetailEntity);
               
            }
        }
        if(detailEntities.size()>0) {
        	this.orderRequestDetailRepo.saveAll(detailEntities);
        	//historry pickup request @Ibnu 30/06/2020
        	for(TPickupOrderRequestDetailEntity pickup : detailEntities) {
        		historyTransactionService.historyRequestPickup(orderRequestEntity, pickup, pickup.getStatus(), userLogin,"");
        	}
        	
        }else {
        	historyTransactionService.historyRequestPickup(orderRequestEntity, null, orderRequestEntity.getStatus(), userLogin,"");
        }
        //end
            
//            orderRequestEntity.setDetailEntityList(detailEntities);
        return RequestPickUpResp.builder()
        		.pickupOrderId(orderRequestEntity.getPickupOrderId())
        		.pickupAddressID(requestPickUpReq.getPickupAddressID())
        		.pickupDate(requestPickUpReq.getPickupDate())
        		.pickupTimeId(requestPickUpReq.getPickupTimeId())
        		.qtyItem(requestPickUpReq.getQtyItem())
        		.paymentType(requestPickUpReq.getPaymentType())
        		.totalPay(requestPickUpReq.getTotalPay())
        		.userId(requestPickUpReq.getUserId())
        		.detail(requestPickUpReq.getDetail())
        		.build();
    }

    public Page<ListBookingCompleteResponse> getListBook(ListBookingCompleteReq bookingCompleteReq, Pageable pageable) throws ParseException {
        LocalDate localDate=DateTimeUtil.getDateFrom(bookingCompleteReq.getDate());
        Page<TPickupOrderRequestDetailEntity> entities=this.orderRequestDetailRepo.findByOrderDateAndPickupTimeEntity(localDate,RequestPickupEnum.IN_WAREHOUSE.getValue(),bookingCompleteReq.getSearchString(),bookingCompleteReq.getOfficeCode(), pageable);
        return new PageImpl<>(
                entities.getContent().stream().map(this::returnDto).collect(Collectors.toList()),
                entities.getPageable(),
                entities.getTotalElements()
        );
    }

    public PickupOrderDetail getPickupOrderDetail(String qrCode){
        TPickupOrderRequestDetailEntity detailEntity=this.orderRequestDetailRepo.findByQrCodeOrQrcodeExt(qrCode, qrCode);
        TWarehouseReceiveDetailEntity warehouseReceiveDetailEntity = whReceiveDetailRepo.findByQrcodeRequest(qrCode).orElseThrow(()-> new NotFoundException("Qrcode Tidak Ditemukan"));
        MOfficeEntity office = officeRepo.findById(warehouseReceiveDetailEntity.getWarehouseReceiveId().getOfficeCode()).orElseThrow(()-> new NotFoundException("Office Code Tidak Ditemukan"));
        PriceDetail price = null;
        if(detailEntity.getProductSwitcherEntity()!=null) {
        	MAreaEntity areaOrigin = mAreaRepo.findByKotaEntityAreaKotaId(detailEntity.getOrderRequestEntity()
            		.getPickupAddressEntity().getPostalCode()
            		.getKecamatanEntity().getKotaEntity().getAreaKotaId());
        	price = priceService.getPricePickup(detailEntity.getProductSwitcherEntity(), 
            		detailEntity.getAreaId(),areaOrigin.getAreaId());
        	price.setSurcharge(warehouseVerification.getListSurcharge(detailEntity.getProductSwitcherEntity().getProductSwCode().intValue()));
        }
        
        return PickupOrderDetail.builder()
                .areaId(detailEntity.getAreaId()==null?null:detailEntity.getAreaId().getAreaDetailId())
                .userId(detailEntity.getOrderRequestEntity().getUserEntity().getUserId())
                .productSwCode(detailEntity.getProductSwitcherEntity()==null?null:detailEntity.getProductSwitcherEntity().getProductSwCode().toString())
                .switcherCode(detailEntity.getProductSwitcherEntity()==null?null:detailEntity.getProductSwitcherEntity().getSwitcherEntity().getSwitcherCode())
                .bookingNumber(detailEntity.getOrderRequestEntity().getPickupOrderId())
                .qty(detailEntity.getQty()==null?null:detailEntity.getQty())
                .weight(detailEntity.getWeight()==null?null:detailEntity.getWeight())
                .receiverName(detailEntity.getNamaPenerima()==null?null:detailEntity.getNamaPenerima())                
				.qrCode(detailEntity.getQrcodeExt()==null?null:detailEntity.getQrcodeExt())
				.originId(areaRepo.getOne(office.getRegionCode()).getKotaEntity().getAreaKotaId())
				.price(price)
                .build();
    }

    private ListBookingCompleteResponse returnDto(TPickupOrderRequestDetailEntity orderRequestEntity){
        return ListBookingCompleteResponse.builder()
                .jmlItem(orderRequestEntity.getQty())
                .vendor(orderRequestEntity.getProductSwitcherEntity()==null?"-":
                        orderRequestEntity.getProductSwitcherEntity().getSwitcherEntity().getDisplayName().concat(" - ")
                                .concat(orderRequestEntity.getProductSwitcherEntity().getDisplayName()))
                .tglPickup(DateTimeUtil.toString(orderRequestEntity.getOrderRequestEntity().getOrderDate()))
//                .timePickup(DateTimeUtil.toString(orderRequestEntity.getOrderRequestEntity().getPickupTimeEntity()))
                .timePickup(null)
                .userId(orderRequestEntity.getOrderRequestEntity().getUserEntity().getUserId())
                .orderId(orderRequestEntity.getOrderRequestEntity().getPickupOrderId())
                .qrCode(orderRequestEntity.getQrcodeExt())
                .courierId(orderRequestEntity.getCreateBy())
                .build();
    }
    
    public Page<ListBookingCompleteResponse> getListRequestPickup(PickupListRequest request,Pageable pageable){
    	MUserEntity mUserEntity = mUserRepo.getOne(request.getUserId());
    	Page<TPickupOrderRequestEntity> lpickupReq = pickupOrderRequestRepo.findAllByUserEntityOrderByPickupOrderId(mUserEntity,pageable);
    	return new PageImpl<>(
                lpickupReq.getContent().stream().map(this::orderReqDto).collect(Collectors.toList()),
                lpickupReq.getPageable(),
                lpickupReq.getTotalElements()
        );
    }
    
    public Page<ListBookingCompleteResponse> getListRequestPickupOrder(PickupOrderListRequest request,Pageable pageable){
    	Page<TPickupOrderRequestEntity> lpickupReq = null;
    	List<Integer> statusReq = null;
    	if(request.getStatus().size() > 0 && request.getStatus().get(0) != null) {
    		statusReq = new ArrayList<>();
    		for(Integer st:request.getStatus()) {
    				if(st != null) 
    				statusReq.add(RequestPickupEnum.toPaymentEnumInteger(st));
    		}
    	}
    	if(request.getQrCode() != null) {
    		lpickupReq = pickupOrderRequestRepo.findAllByOrderByOrderDateDesc(request.getUserId(),
        			request.getOrderDate(),
        			statusReq,
        			request.getQrCode(),
        			request.getFilter(),
        			pageable);
    	}else {
    		lpickupReq = pickupOrderRequestRepo.findAllByOrderIdByOrderDateDesc(request.getUserId(),
    				request.getOrderDate(),
    				statusReq,
    				request.getFilter(),
    				pageable);
    	}
    	
    	return new PageImpl<>(
                lpickupReq.getContent().stream().map(this::orderReqDto).collect(Collectors.toList()),
                lpickupReq.getPageable(),
                lpickupReq.getTotalElements()
        );
    }
    private ListBookingCompleteResponse orderReqDto(TPickupOrderRequestEntity entity) {
    	
    	return ListBookingCompleteResponse.builder()
    			.jmlItem(entity.getQty())
    			.orderId(entity.getPickupOrderId())
    			.timePickup(entity.getPickupTimeEntity()==null?""+entity.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")):entity.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+" "+DateTimeUtil.toString(entity.getPickupTimeEntity()))
    			.status(RequestPickupEnum.toPaymentEnum(entity.getStatus()).getCodeString())
    			.statusDec(RequestPickupEnum.getPaymentEnum(entity.getStatus()).name())
    			.address((entity.getPickupAddressEntity()==null)?"":entity.getPickupAddressEntity().getAddress())
    			.description(entity.getPickupAddressEntity()==null?"":entity.getPickupAddressEntity().getDescription())
    			.kelurahan(entity.getPickupAddressEntity()==null?"":entity.getPickupAddressEntity().getPostalCode().getKelurahan())
    			.kecamatan(entity.getPickupAddressEntity()==null?"":entity.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKecamatan())
    			.kota(entity.getPickupAddressEntity()==null?"":entity.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
    			.createBy(entity.getCreateBy())
    			.userId(entity.getUserEntity()==null?"":entity.getUserEntity().getUserId())
    			.build();
    }
    
    @Transactional(rollbackOn=Exception.class)
    public Response<String> cancelRequestPickup(String userId,String pickupOrderId,String qrcode){
    	MUserEntity user = mUserRepo.getOne(userId);
    	TPickupOrderRequestEntity pickupEntity = pickupOrderRequestRepo.findByUserEntityAndPickupOrderId(user, pickupOrderId);
    	Integer countDetail = orderRequestDetailRepo.countByOrderRequestEntity(pickupEntity);
    	MUserPriorityEntity userPriority = mUserPriorityRepo.findByUserCategory(user.getUserCategory().getSeqid());
    	if(pickupEntity==null)throw new NotFoundException("Data Tidak Ditemukan !");
    	if(pickupEntity.getStatus().equals(RequestPickupEnum.IN_COURIER.getValue())
    			|| pickupEntity.getStatus().equals(RequestPickupEnum.IN_WAREHOUSE.getValue())
    			|| pickupEntity.getStatus().equals(RequestPickupEnum.FINISH_BOOK.getValue())
    			|| pickupEntity.getStatus().equals(RequestPickupEnum.CANCEL_DETAIL.getValue())) {
    		throw new NotFoundException("Pesanan tidak bisa dibatalkan !");
    	}
    	Integer oldStatus = pickupEntity.getStatus();
    	if(!qrcode.equals("-")) {
    		if(countDetail <= userPriority.getMinKiriman()) {
        		throw new NotFoundException("Pesanan kurang dari minimal kiriman :"+userPriority.getMinKiriman());
        	}
    	}
    	
    	List<TPickupOrderRequestDetailEntity> lPickupdtl = orderRequestDetailRepo.findAllByOrderRequestEntity(pickupEntity);
    	Boolean flag = false;
    	for(TPickupOrderRequestDetailEntity dtl : lPickupdtl) {
    		if(dtl.getQrCode().equals(qrcode) || lPickupdtl.size() == 1) {
    			Integer status = dtl.getStatus();
    			dtl.setStatus(RequestPickupEnum.CANCEL_DETAIL.getValue());
        		dtl.setUpdateBy(userId);
        		dtl.setUpdateDate(LocalDateTime.now());
        		orderRequestDetailRepo.save(dtl);
        		//get Mutasi
        		if(dtl.getIsPay().equals(ISPAY)) {
    				insertTCredit(dtl, user,new BigDecimal("-1"));
    				TMutasiEntity mutasi = insertMutasi(dtl, "RFN"+dtl.getOrderRequestEntity().getPickupOrderId(), 
    	    				MutasiEnum.REFUND.getKeterangan().concat(" ").concat(dtl.getOrderRequestEntity().getPickupOrderId()),MutasiEnum.REFUND);
    	    		user.setBalance(mutasi.getSaldo());
    	    		tMutasiRepo.save(mutasi);
    	    		mUserRepo.save(user);
    			}
        		historyTransactionService.historyRequestPickup(pickupEntity, dtl, status, userId, "");
        		flag=true;
    		}    		
    	}
    	if(lPickupdtl.size() <= 1) {
    		pickupEntity.setStatus(RequestPickupEnum.CANCEL_DETAIL.getValue());
        	pickupEntity.setUpdateBy(userId);
        	pickupEntity.setUpdateDate(LocalDateTime.now());
        	pickupOrderRequestRepo.save(pickupEntity);
        	historyTransactionService.historyRequestPickup(pickupEntity, null, oldStatus, userId, "");
        	flag=true;
        	TPickupDetailEntity pickupDtl = tPickupDetailRepo.findByPickupOrderRequestEntity(pickupEntity);
        	if(pickupDtl != null) {
        		pickupDtl.setStatus(PickupDetailEnum.HISTORY.getValue());
        		tPickupDetailRepo.save(pickupDtl);
        		Boolean flagpickup = false;
				List<TPickupDetailEntity> lpickup = tPickupDetailRepo.findByPickupId(pickupDtl.getPickupId());
				for(TPickupDetailEntity dtl:lpickup) {
					if(dtl.getStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getValue())) {
						flagpickup=true;
						break;
					}
				}
				if(!flagpickup) {
					for(TPickupDetailEntity dtl:lpickup) {
    					if(dtl.getStatus().equals(PickupDetailEnum.IN_COURIER.getValue())) {
    						flagpickup=true;
    						dtl.getPickupId().setStatus(PickupEnum.IN_COURIER.getValue());
    						tPickupDetailRepo.save(dtl);
    						break;
    					}
    				}
				}
				if(!flagpickup) {
					pickupDtl.getPickupId().setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());
					tPickupDetailRepo.save(pickupDtl);
				}
        	}
    	}    	
    	if(!flag) {
    		throw new NotFoundException("Gagal Batalkan Pesanan !");
    	}

		pickupDetailRepo.findByPickupOrderRequestEntityPickupOrderId(pickupOrderId).ifPresent(pickupDetail -> bookService.verifyCourierPickup(pickupDetail));


		return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase()
    			);
    }

    private void insertTCredit(TPickupOrderRequestDetailEntity pay, MUserEntity user, BigDecimal mat) {
		if(DepositTypeEnum.CREDIT
				==DepositTypeEnum.getDepositTypeEnum(user.getDepositType())) {
			TCreditEntity credit = tCreditRepo.findByTglAndUserIdAndFlag(pay.getCreateDate().toLocalDate(), user.getUserId(), FLAG);
			BigDecimal nominal = credit.getNominal().add(pay.getAmount().multiply(mat));
			credit.setNominal(nominal);
			tCreditRepo.save(credit);
		}
		
	}
    
    private TMutasiEntity insertMutasi(TPickupOrderRequestDetailEntity pickupDtl,String trxNo,String description,MutasiEnum type) {
    	BigDecimal saldo = new BigDecimal(bookService.getSaldoMutasiByUserId(pickupDtl.getOrderRequestEntity().getUserEntity()));
    	BigDecimal amount = pickupDtl.getAmount();
    	if(type.equals(MutasiEnum.REFUND)) {
    		amount = amount.multiply(new BigDecimal(-1));
    	}
    	saldo = saldo.add(amount);
    	return TMutasiEntity.builder()
    			.trxNo(trxNo)
    			.amount(amount)
    			.customerId(pickupDtl.getOrderRequestEntity().getUserEntity().getUserId())
    			.productSwCode(pickupDtl.getProductSwitcherEntity().getProductSwCode().toString())
    			.descr(description)
    			.saldo(saldo)
    			.trxType(type.getCode())
    			.trxDate(LocalDate.now())
    			.trxTime(LocalTime.now())
    			.userId(pickupDtl.getOrderRequestEntity().getUserEntity())
    			.updateBy(pickupDtl.getOrderRequestEntity().getUserEntity().getUserId())
    			.trxServer(LocalDateTime.now())
    			.build();
    }
    
    public TPickupOrderRequestDetailEntity getPickupOrderDetail(DetailBooks detail) {
    	return orderRequestDetailRepo
    			.findByOrderRequestEntityAndQrCode(pickupOrderRequestRepo.findByPickupOrderId(detail.getBookingCode()), detail.getQrcode());
    }
    
    public List<TPickupOrderRequestDetailEntity> getPickupOrderReqDetail(RequestPickupEnum requestPickupEnum,LocalDate date,LocalTime time){
    	return orderRequestDetailRepo.findAllByStatusAndIsPay(requestPickupEnum.getValue(), date, time);
    }
    
    public List<TPickupOrderRequestDetailEntity> getExpiredPickupReq(RequestPickupEnum requestPickupEnum,LocalDate date,LocalTime time){
    	return orderRequestDetailRepo.findAllByStatusPending(requestPickupEnum.getValue(), date, time);
    }
    
}
