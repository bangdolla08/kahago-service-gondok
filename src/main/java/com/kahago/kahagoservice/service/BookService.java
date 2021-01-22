
package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.*;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.BookRequest;
import com.kahago.kahagoservice.model.request.CancelBookReq;
import com.kahago.kahagoservice.model.request.CompleteBookReq;
import com.kahago.kahagoservice.model.request.DetailBooking;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.RespUncomplete;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MCounterRepo;
import com.kahago.kahagoservice.repository.MGoodsRepo;
import com.kahago.kahagoservice.repository.MPickupTimeRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MReceiverRepo;
import com.kahago.kahagoservice.repository.MSenderRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TOfficeRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupAddressRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveDetailRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;
import com.kahago.kahagoservice.util.UniqueRandom;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Service
public class BookService {

	private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private static final String FLAG = "0";
    private static final int ACTIVE = 1;
    private static final double PEMBAGI_PACKING = 11.5;
    private static final int HARGA_PACKING = 50000;
    private static final int INSURANCE_MINIMUM = 5000;
    private static final int USER_CATEGORY_COUNTER = 0;

    @Autowired
    private MSenderRepo mastSenderRepo;
    @Autowired
    private TPickupOrderRequestDetailRepo orderRequestDetailRepo;
    @Autowired
    private TPickupOrderRequestRepo orderRequestRepo;
    @Autowired
    private MReceiverRepo mastReceiverRepo;
    @Autowired
    private TPaymentRepo transPaymentRepo;
    @Autowired
    private MProductSwitcherRepo mastProductSwitcherRepo;
    @Autowired
    private MGoodsRepo mastGoodsRepo;
    @Autowired
    private MPickupTimeRepo mastPickupTimeRepo;
    @Autowired
    private THistoryBookRepo transHistoryBookRepo;
    @Autowired
    private MUserRepo mastUserRepo;
    @Autowired
    private MAreaRepo mAreaRepo;
    @Autowired
    private TCreditRepo transCreditRepo;
    @Autowired
    private TMutasiRepo transMutasiRepo;
    @Autowired
    private MUserRepo userRepo;
    @Autowired
    private MPostalCodeRepo postalCodeRepo;
    @Autowired
    private TPickupAddressRepo pickupAddressRepo;
    @Autowired
    private TWarehouseReceiveDetailRepo warehouseDetailRepo;
    @Autowired
    private TAreaRepo areaRepo;
    @Getter @Setter
    private Double tarif;
    @Autowired
    private TOfficeRepo officeRepo;
    @Autowired
    private MCounterRepo counterRepo;
    @Autowired
    private MPostalCodeRepo mPostalCode;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MFreedateRepo mFreedateRepo;
    @Autowired
    private MFreedayRepo mFreedayRepo;
    @Autowired
    private MAreaRepo originRepo;
    @Autowired
    private TPickupDetailRepo pickupDetailRepo;
    @Autowired
    private HistoryTransactionService historyTransactionService;
    @Autowired
    private MPickupTimeRepo mPickupTimeRepo;

    @Autowired
    private MOfficeRepo mOfficeRepo;
    private static final Integer STATUS_BAYAR = 1;
    @Autowired
    private THppPaymentRepo hppRepo;
    @Autowired
    private TPickupOrderHistoryRepo tPickupHistory;
    @Autowired
    private TPaymentHistoryRepo tPaymentHistoryRepo;
    @Autowired
    private TCourierPickupRepo courierPickupRepo;
    /**
     * @param request
     * @return
     */
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public BookResponse booking(BookRequest request,String header) {
    	DeviceEnum devEnum = Common.getDevice(header);
        String bookingCode = generateBookingCode();
        MUserEntity user = userRepo.getMUserEntitiesBy(request.getUserId());
        MProductSwitcherEntity productSwitcher = mastProductSwitcherRepo.findById(Long.valueOf(request.getProductCode())).get();//getFilterPriceByProduct(Integer.valueOf(request.getProductCode()), request.getDestination(), request.getTotalGrossWeight());
        MGoodsEntity commodity = getCommodity(request.getComodity());
        MPickupTimeEntity pickupTime = getPickupTimeById(request.getIdPickupTime());
        List<DetailBooking> bookDetails = recalculateDetailBooking(request.getDetailBooking(), productSwitcher.getPembagiVolume());
        String origin = originRepo.findByKotaEntityAreaKotaId(pickupAddressRepo.findById(Integer.valueOf(request.getPickupId())).get()
        		.getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId()).getAreaId();
        MPostalCodeEntity areaid = postalCodeRepo.findById(Integer.valueOf(request.getReceiverPostalCode())).orElseThrow(()-> new NotFoundException("Area Not Found"));
        List<TAreaEntity> lsArea = areaRepo.findTOPByProductSwCodeAndMPostalCodeAndAreaOriginIdOrderByTarifAsc(productSwitcher, areaid.getKecamatanEntity(),origin);
//        lsArea.stream().filter(p -> p.getTarif()!=null).sorted((o1,o2)->o1.getTarif().compareTo(o2.getTarif())).collect(Collectors.toList());
        Collections.sort(lsArea,new Comparator<TAreaEntity>() {
            @Override public int compare(TAreaEntity p1, TAreaEntity p2) {
                return p1.getTarif().compareTo(p2.getTarif()); // Ascending
            }
        });
        TAreaEntity area = lsArea
    			.stream().findFirst().get();
    	this.tarif = area.getTarif().doubleValue() / area.getMinimumKg();
        Map weights = mapAllWeights(request, bookDetails,productSwitcher.getPembulatanVolume());
        Map rates = mapAllPrices(request, productSwitcher, bookDetails, weights, lsArea.get(0));

        String total = String.valueOf(Math.round((Double) rates.get("total")));
        double ttl = Double.valueOf(total);
        double ttlPrice = Double.valueOf(request.getTotalPrice());
        //sementara
        if(ttlPrice != ttl){
        	log.error("Terjadi Perbedaan Total Price:");
        	rates.put("total", ttlPrice);
        }
        if(ttlPrice==0) rates.put("total", ttl);
        double balance = calculateSaldo((Double) rates.get("total"), request.getUserId());

        if(LocalDate.parse(request.getPickupDate()).isEqual(LocalDate.of(2020, 5, 20))
        		&& pickupTime.getTimeFrom().isAfter(LocalTime.of(12, 0))
        		&& (productSwitcher.getSwitcherEntity().getSwitcherCode()==310
        		|| productSwitcher.getSwitcherEntity().getSwitcherCode()==302)) {
        	throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Produk sudah melewati jangka waktu Pickup");
        }

        TPaymentEntity payment = initializePayment(request, bookingCode, rates, bookDetails, productSwitcher, pickupTime, commodity, weights,devEnum.getCodeString());
        payment.setStatus(PaymentEnum.PENDING.getCode());
        THistoryBookEntity historyBook = initializeHistoryBook(bookingCode, bookDetails.size(), request.getUserId(),"Permintaan Pemesanan","-");

        checkSenderAndSave(request);
        checkReceiverAndSave(request);
        if(devEnum==DeviceEnum.WEB
        		&& PayTypeEnum.getEnum(Integer.valueOf(request.getPayType()))==PayTypeEnum.PAY_NOW) {

        	if (balance < 0 ) {
                throw new NotFoundException("Saldo Anda Tidak Cukup");
            }
        	payment.setStatus(PaymentEnum.REQUEST.getCode());
        	payment.setOfficeCode(request.getOfficeCode());
//        	if(request.getOfficeCode()==null) {
//        		TOfficeEntity officeEntity = officeRepo.findByUserId(payment.getUserId().getUserId()).stream().findFirst().orElse(new TOfficeEntity());
//        		this.mOffice = officeEntity.getOfficeCode();
//        	}else {
//        		this.mOffice = mOfficeRepo.findById(request.getOfficeCode()).get();
//        	}
//        	if(OfficeTypeEnum.COUNTER==OfficeTypeEnum.getPaymentEnum(office.getUnitType())) {
//        		payment.setStatus(PaymentEnum.RECEIVE_IN_COUNTER.getCode());
//        		payment.setOfficeCode(request.getOfficeCode());
//        	}
        	payment = getPaymentWEB(payment);
        }
        payment.setTbooks(initializeBooks(bookDetails, payment));
        transHistoryBookRepo.save(historyBook);
        transPaymentRepo.save(payment);
        historyTransactionService.createHistory(payment, payment, payment.getUserId().getUserId());
        this.insertHpp(payment);
        String waktu = "";
        try {
			waktu = DateTimeUtil.getTimeCustom(payment.getPickupTimeId().getTimeFrom().toString().concat(":00"), -10, "kk:mm:ss");
			waktu = DateTimeUtil.getString2Date(payment.getPickupDate().toString(),"yyyy-MM-dd", "dd-MM-yyyy")
					.concat(" ").concat(waktu);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return BookResponse.builder()
                .bookingCode(bookingCode)
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .senderName(request.getSenderName())
                .receiverName(request.getReceiverName())
                .urlResi(Common.getResi(payment))
                .timeLimit(waktu)
                .build();
    }
    @Transactional
    public SaveResponse complateBookingReq(CompleteBookReq request, String userLogin){
    	TPaymentEntity payQr = transPaymentRepo.findFirstByQrCodeExt(request.getQrCode());
    	if(payQr!=null) {
    		return SaveResponse.builder()
            		.saveStatus(1)
            		.saveInformation("Transaksi Sudah Ada")
            		.linkResi(Common.getResi(payQr))
            		.results(Arrays.asList(RespUncomplete.builder()
            				.bookingCode(payQr.getBookingCode())
            				.qrcode(payQr.getQrcodeExt()).build()))
            		.build();
    	}
        String bookingCode = generateBookingCode();
        if(request.getProductCode() == null)throw new NotFoundException("Vendor Tidak Boleh Kosong !");
        MProductSwitcherEntity productSwitcher = mastProductSwitcherRepo.findById(Long.valueOf(request.getProductCode())).get();
        MGoodsEntity commodity = getCommodity(request.getGoodsId());
        List<DetailBooking> bookDetails = recalculateDetailBooking(request.getDetailBooking(), productSwitcher.getPembagiVolume());
        TPickupOrderRequestDetailEntity orderRequestDetailEntity=orderRequestDetailRepo.findByQrCodeOrQrcodeExt(request.getQrCode(), request.getQrCode());
        String origin = originRepo.findByKotaEntityAreaKotaId(request.getOriginId()).getAreaId();
        MPostalCodeEntity areaid = postalCodeRepo.findById(request.getIdPostalCode()).get();

        List<TAreaEntity> lsArea = areaRepo.findTOPByProductSwCodeAndMPostalCodeAndAreaOriginIdOrderByTarifAsc(productSwitcher, areaid.getKecamatanEntity(),origin);
        Collections.sort(lsArea,new Comparator<TAreaEntity>() {
            @Override public int compare(TAreaEntity p1, TAreaEntity p2) {
                return p1.getTarif().compareTo(p2.getTarif()); // Ascending
            }
        });
        TAreaEntity area = lsArea
                .stream().findFirst().get();
        this.tarif = area.getTarif().doubleValue() / area.getMinimumKg();
        Map weights = mapAllWeights(request.getIsPackingKayu(), bookDetails,productSwitcher.getPembulatanVolume());
        Map rates = mapAllPrices(request.getIsPackingKayu(),request.getIsAsuransi(),request.getGoodsId(),request.getGoodsPrice(), productSwitcher, bookDetails, weights,lsArea
                .stream().findFirst().get());
        if(orderRequestDetailEntity.getOrderRequestEntity().getPickupTimeEntity() == null) {
        	orderRequestDetailEntity.getOrderRequestEntity().setPickupTimeEntity(mPickupTimeRepo.findByIdPickupTime(1));
        }
        TPaymentEntity payment =initializePayment(request, bookingCode, rates, bookDetails, productSwitcher,
        		orderRequestDetailEntity.getOrderRequestEntity().getPickupTimeEntity(), commodity,
        		weights,orderRequestDetailEntity,lsArea.get(0));
        String officeCode = "";
        List<TOfficeEntity> lOffice = officeRepo.findByUserIdUserId(request.getUserAdmin());
        officeCode = lOffice.get(0).getOfficeCode().getOfficeCode();
        payment.setOfficeCode(officeCode);
        payment.setQrcodeExt(orderRequestDetailEntity.getQrcodeExt());
        payment.setTbooks(initializeBooks(bookDetails, payment));
        THistoryBookEntity historyBook = initializeHistoryBook(bookingCode, bookDetails.size(), payment.getUserId().getUserId(),"Permintaan Pemesanan","-");
        transPaymentRepo.saveAndFlush(payment);
        createHistoryPickupRequest(payment, orderRequestDetailEntity);
        this.historyTransactionService.createHistoryRequest(payment, payment, userLogin, orderRequestDetailEntity);
        transHistoryBookRepo.save(historyBook);
        this.insertHpp(payment);
        //update pickup order detail @Ibnu
        orderRequestDetailEntity.setBookCode(payment.getBookingCode());
        orderRequestDetailEntity.setStatus(RequestPickupEnum.FINISH_BOOK.getValue());
        orderRequestDetailEntity.setNamaPenerima(payment.getReceiverName());
        orderRequestDetailEntity.setProductSwitcherEntity(payment.getProductSwCode());
        orderRequestDetailEntity.setAreaId(payment.getIdPostalCode().getKecamatanEntity());
        orderRequestDetailEntity.setWeight(payment.getGrossWeight().doubleValue());
        orderRequestDetailEntity.setIdPayment(payment.getIdPayment());
        orderRequestDetailEntity.setAmount(payment.getAmount());
        orderRequestDetailEntity.setPaymentOption(payment.getPaymentOption());
        orderRequestDetailEntity.setCountPawoon(payment.getCountPawoon());
        //end update
        orderRequestDetailEntity=orderRequestDetailRepo.saveAndFlush(orderRequestDetailEntity);
        Integer countAllBook=orderRequestDetailRepo.countByOrderRequestEntity(orderRequestDetailEntity.getOrderRequestEntity());
        Integer countBookInFinishBook=orderRequestDetailRepo.countByOrderRequestEntityAndStatus(orderRequestDetailEntity.getOrderRequestEntity(),RequestPickupEnum.FINISH_BOOK.getValue());
        TPickupOrderRequestEntity orderRequestEntity=orderRequestDetailEntity.getOrderRequestEntity();
        if(countAllBook.equals(countBookInFinishBook)){
            orderRequestEntity.setStatus(RequestPickupEnum.FINISH_BOOK.getValue());
            orderRequestRepo.save(orderRequestEntity);
        }

        //update book id to warehouse
        TWarehouseReceiveDetailEntity warehouseDetail = warehouseDetailRepo
        		.findByQrcodeRequest(payment.getQrcodeExt()).get();
        warehouseDetail.setBookId(payment);
        warehouseDetailRepo.save(warehouseDetail);
        return SaveResponse.builder()
        		.saveStatus(1)
        		.saveInformation("Berhasil")
        		.linkResi(Common.getResi(payment))
        		.results(Arrays.asList(RespUncomplete.builder()
        				.bookingCode(payment.getBookingCode())
        				.qrcode(payment.getQrcodeExt()).build()))
        		.build();
    }



	public TPaymentEntity getPaymentWEB(TPaymentEntity payment) {
		// TODO Auto-generated method stub
//		payment.setStatus(PaymentEnum.REQUEST.getCode());
		MUserEntity user = payment.getUserId();
//		if(DepositTypeEnum.CREDIT==DepositTypeEnum.getDepositTypeEnum(payment.getUserId().getDepositType())) {

//		}
//		Double saldo = getSaldoMutasiByUserId(payment.getUserId());

		//validasi user credit yang masih memiliki tagihan
		if(user.getDepositType().equals(DepositTypeEnum.CREDIT.getValue())) {
			List<TCreditEntity> lcredit = transCreditRepo.findByUserAndNominalGraterZero(user.getUserId(), "0");
			if(lcredit.size() > 0) {
				if(lcredit.get(0).getCreditDay().compareToIgnoreCase(user.getCreditDay()) >0) {
					throw new InternalServerException("Masih terdapat tagihan yang belum terbayarkan !");
				}
			}
		}
		TMutasiEntity mutasi = insertMutasi(payment, payment.getBookingCode(), MutasiEnum.BOOKING.getKeterangan().concat(" Via DOMPET"),MutasiEnum.BOOKING);
		payment.setStatusPay(StatusPayEnum.PAID.getCode());
		payment.setPaymentOption("dompet");
		transPaymentRepo.save(payment);
		insertTCredit(payment, user, payment.getAmount());
		payment = insertWarehouse(payment, user, PaymentEnum.REQUEST);
		transMutasiRepo.saveAndFlush(mutasi);
		userRepo.save(updateBalanceUser(payment.getUserId().getUserId(), mutasi.getSaldo().doubleValue()));

		return payment;
	}


	public TPaymentEntity insertWarehouse(TPaymentEntity payment, MUserEntity user, PaymentEnum payStat) {
		payStat = getStatusPayment(payment, payStat);
		payment.setStatus(payStat.getCode());
		List<TOfficeEntity> tOffice = officeRepo.findByUserIdUserId(user.getUserId());
//		if(this.mOffice==null) return payment;
		MOfficeEntity office = mOfficeRepo.findByOfficeCode(payment.getOfficeCode());
		if(tOffice.size() > 0) {
			if(user.getUserCategory().getSeqid().equals(USER_CATEGORY_COUNTER)) {
				office = tOffice.get(0).getOfficeCode();
			}
		}

		if(office==null) return payment;
		if(OfficeTypeEnum.WAREHOUSE== OfficeTypeEnum.getPaymentEnum(office.getUnitType())  && payStat==PaymentEnum.REQUEST) {
//			String office = officeRepo.findByUserId(payment.getUserId().getUserId()).stream().findFirst().get().getOfficeCode().getOfficeCode();
			String kode = getCounterWarehouse(office.getOfficeCode());
			payment.setOfficeCode(office.getOfficeCode());
			if(isInsertWarehouse(kode,payment)) payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
		}else if(OfficeTypeEnum.COUNTER== OfficeTypeEnum.getPaymentEnum(office.getUnitType())) {
			payment.setStatus(PaymentEnum.RECEIVE_IN_COUNTER.getCode());
			payment.setOfficeCode(office.getOfficeCode());
		}
//		if(payment.getStatus()==PaymentEnum.RECEIVE_IN_COUNTER.getCode()) return payment;
		return payment;
	}

	public Boolean isInsertWarehouse(String kode,TPaymentEntity pay) {
		// TODO Auto-generated method stub
		TWarehouseReceiveEntity whReceive = TWarehouseReceiveEntity.builder()
				.code(kode)
				.createBy(pay.getUserId().getUserId())
				.createDate(LocalDateTime.now())
				.officeCode(pay.getOfficeCode())
				.build();
//		warehouseRepo.save(whReceive);
		TWarehouseReceiveDetailEntity whReceiveDetail = TWarehouseReceiveDetailEntity
				.builder()
				.warehouseReceiveId(whReceive)
				.bookId(pay)
				.status(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode())
				.createBy(pay.getUserId().getUserId())
				.createAt(LocalDateTime.now())
				.build();
		warehouseDetailRepo.save(whReceiveDetail);

		return true;

	}


	public String getCounterWarehouse(String office) {
		// TODO Auto-generated method stub
		String prefix = "C"+office;
		MCounterEntity counter = counterRepo.findAll().stream().findFirst().get();
		Integer count = counter.getWarehouse();
		count++;
		prefix+=count.toString();
		counter.setWarehouse(count);
		counterRepo.save(counter);
		return prefix;
	}



	@Transactional
    public Response<String> cancelBook(CancelBookReq request) {
    	TPaymentEntity pay = transPaymentRepo.findById(request.getBookingCode()).get();
    	TPaymentEntity oldPay = pay;
    	PaymentEnum payEnum = PaymentEnum.getPaymentEnum(pay.getStatus());
    	if(payEnum.getCode() > PaymentEnum.ASSIGN_PICKUP.getCode()
    			&& payEnum.getCode() <= PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode()	) {
    		return new Response<>(ResponseStatus.IN_PROCCESS.value(),
    				ResponseStatus.IN_PROCCESS.getReasonPhrase());
    	}else if(payEnum.getCode() > PaymentEnum.CANCEL_BY_WAREHOUSE.getCode()
    				&& payEnum.getCode() <= PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode()
    				|| payEnum==PaymentEnum.CANCEL_BY_USER) {
    		return new Response<>(ResponseStatus.NOT_PROCESS.value(),
    				ResponseStatus.NOT_PROCESS.getReasonPhrase());
    	}
    	else if(payEnum==PaymentEnum.PENDING) {
    		pay.setStatus(PaymentEnum.CANCEL_BY_USER.getCode());
    		transPaymentRepo.save(pay);
    	}else {
    		if(payEnum==PaymentEnum.ASSIGN_PICKUP
    				|| payEnum==PaymentEnum.DRAFT_PICKUP ) {
    			TPickupDetailEntity tpickup = pickupDetailRepo.findByBookId(pay).stream().findAny().get();
    			tpickup.setStatus(PickupDetailEnum.HISTORY.getValue());
    			pickupDetailRepo.save(tpickup);
    			if(tpickup != null) {
    				Boolean flag = false;
    				List<TPickupDetailEntity> lpickup = pickupDetailRepo.findByPickupId(tpickup.getPickupId());
    				for(TPickupDetailEntity dtl:lpickup) {
    					if(dtl.getStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getValue())) {
    						flag=true;
    						break;
    					}
    				}
    				if(!flag) {
    					for(TPickupDetailEntity dtl:lpickup) {
        					if(dtl.getStatus().equals(PickupDetailEnum.IN_COURIER.getValue())) {
        						flag=true;
        						dtl.getPickupId().setStatus(PickupEnum.IN_COURIER.getValue());
        						pickupDetailRepo.save(dtl);
        						break;
        					}
        				}
    				}
    				if(!flag) {
    					tpickup.getPickupId().setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());
    					pickupDetailRepo.save(tpickup);
    				}
    			}
    			 TPickupDetailEntity pickupDetail = pickupDetailRepo.findByBookIdBookingCode(request.getBookingCode()).orElseThrow(NotFoundException::new);
    		     verifyCourierPickup(pickupDetail);

    		}
    		MUserEntity user = pay.getUserId();
    		insertTCredit(pay, user,new BigDecimal("-1"));
    		TMutasiEntity mutasi = insertMutasi(pay, "RFN"+pay.getBookingCode(),
    				MutasiEnum.REFUND.getKeterangan().concat(" ").concat(pay.getBookingCode()),MutasiEnum.REFUND);
    		user.setBalance(mutasi.getSaldo());
    		pay.setStatus(PaymentEnum.CANCEL_BY_USER.getCode());
    		transPaymentRepo.save(pay);
    		transMutasiRepo.save(mutasi);
    		userRepo.save(user);
    	}
    	historyTransactionService.createHistory(oldPay, pay, pay.getUserId().getUserId());

       

    	return new Response<>(ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase());
    }

    public void verifyCourierPickup(TPickupDetailEntity pickupDetail) {
        PickupCourierEnum pickupCourierEnum = revalidatedLastPickupCourier(pickupDetail);
        TCourierPickupEntity courierPickup = courierPickupRepo.findByPickupIdAndPickupAddressId(
                pickupDetail.getPickupId().getIdPickup(),
                pickupDetail.getPickupAddrId().getPickupAddrId());

        saveCourierPickup(courierPickup, pickupCourierEnum);
    }

    private void saveCourierPickup(TCourierPickupEntity courierPickup, PickupCourierEnum pickupCourierEnum) {
        courierPickup.setStatus(pickupCourierEnum.getValue());
        courierPickupRepo.save(courierPickup);
    }

    private PickupCourierEnum revalidatedLastPickupCourier(TPickupDetailEntity pickupDetail) {
        List<Integer> pickupDetails = pickupDetailRepo.findByPickupIdAndPickupAddrAndStatus(pickupDetail.getPickupId().getIdPickup(), pickupDetail.getPickupAddrId().getPickupAddrId(), PickupDetailEnum.HISTORY.getValue())
                .stream().map(TPickupDetailEntity::getStatus)
                .collect(Collectors.toList());

        if (pickupDetails.contains(PickupDetailEnum.REJECTED_PICKUP.getValue())){
            return PickupCourierEnum.ISSUES_IN_ADDRESS;
        } else if (pickupDetails.contains(PickupDetailEnum.ASSIGN_PICKUP.getValue())){
            return PickupCourierEnum.READY_PICKUP;
        } else if (pickupDetails.contains(PickupDetailEnum.IN_COURIER.getValue())){
            return PickupCourierEnum.FINISH_PICKUP;
        } else {
            return PickupCourierEnum.FINISH;
        }
    }

    public void insertTCredit(TPaymentEntity pay, MUserEntity user, BigDecimal mat) {
		if(user.getDepositType().equals(DepositTypeEnum.CREDIT.getValue())
				&& pay.getPaymentOption().equalsIgnoreCase("dompet")) {
			TCreditEntity credit = transCreditRepo.findByTglAndUserIdAndFlag(pay.getTrxDate(), user.getUserId(), FLAG);
			if(credit==null) {
				credit = transCreditRepo.findByUserIdAndFlag(user.getUserId(), FLAG).orElse(new  TCreditEntity().builder()
						.nominal(new BigDecimal("0")).build());
				if(credit.getNominal().doubleValue()>0) {
					credit = TCreditEntity.builder()
							.flag("0")
							.userId(user.getUserId())
							.nominal(new BigDecimal("0"))
							.tgl(LocalDate.now())
							.tglMulai(credit.getTglMulai())
							.tglSelesai(credit.getTglSelesai())
							.creditDay(String.valueOf(Integer.valueOf(checkValidateCredit(user))))
							.build();
				}else {
					credit = TCreditEntity.builder()
							.flag("0")
							.userId(user.getUserId())
							.nominal(new BigDecimal("0"))
							.tgl(LocalDate.now())
							.tglMulai(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
							.tglSelesai(LocalDate.now().plusDays(Integer.valueOf(user.getCreditDay())).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
							.creditDay(user.getCreditDay())
							.build();
				}
			}
			BigDecimal nominal = credit.getNominal().add(pay.getAmount());
			credit.setNominal(nominal);
			transCreditRepo.save(credit);
		}

	}
    private Map mapAllPrices(BookRequest request, MProductSwitcherEntity productSwitcher, List<DetailBooking> bookDetails, Map weights, TAreaEntity area) {
        Map<String, Double> rates = new HashMap<>();
        double pricePacking = 0;

        double priceInsurance = 0;
        if (Double.valueOf(request.getTotalInsurance()) > 0) {
            priceInsurance = calculatesPriceOfInsurance(mastGoodsRepo.findById(request.getComodity()).get().getInsuranceValue().doubleValue(),
            		Double.valueOf(request.getPriceGoods()));
        }
        rates.put("insurance", priceInsurance);

        double priceSurcharge = calculatesPriceOfSurcharge(bookDetails, productSwitcher.getKgSurcharge(), getTarif());
        rates.put("surcharge", priceSurcharge);
        Double weight = (area.getMinimumKg()>(Double) weights.get("betterWeight"))?area.getMinimumKg():(Double) weights.get("betterWeight");
//        Double weight = (Double) weights.get("betterWeight");
//        if(weight.intValue() <= area.getMinimumKg()) {
//        	this.tarif = area.getTarif().divide(new BigDecimal(request.getMinWeight()), RoundingMode.CEILING).doubleValue();
//        }else if(weight.intValue() > area.getMinimumKg()){
//        	Integer divWeight = request.getMinWeight() - area.getMinimumKg();
//        	this.tarif = area.getTarif().add(area.getNextRate().multiply(new BigDecimal(divWeight))).doubleValue();
//        	this.tarif = getTarif()/request.getMinWeight();
//        	
//        }
        if (Double.valueOf(request.getTotalPackingPrice()) > 0) {
            pricePacking = calculatesPriceOfPacking(bookDetails, tarif);
        }
        rates.put("packing", pricePacking);
        double price = calculatesPrice(weight, tarif);
        rates.put("price", price);
        Boolean isPack = (Integer.valueOf(request.getTotalPackKg())>0)?true:false;
        double extraCharge = calculatesExtraCharge(isPack,HARGA_PACKING,bookDetails.size());
        rates.put("exCharge", extraCharge);

        double grandTotal = price + priceInsurance + priceSurcharge + pricePacking;
        rates.put("total", grandTotal);

        return rates;
    }
    public Map mapAllPrices(Boolean isPacking,Boolean isInsurance,Long comodity,String priceGoods, MProductSwitcherEntity productSwitcher,
    		List<DetailBooking> bookDetails, Map weights, TAreaEntity area) {
        Map<String, Double> rates = new HashMap<>();
        double pricePacking = 0;
        if (isPacking) {
            pricePacking = calculatesPriceOfPacking(bookDetails, tarif);
        }
        rates.put("packing", pricePacking);

        double priceInsurance = 0;
        if (isInsurance) {
            priceInsurance = calculatesPriceOfInsurance(mastGoodsRepo.findById(comodity).get().getInsuranceValue().doubleValue(),
                    Double.valueOf(priceGoods));
        }
        rates.put("insurance", priceInsurance);

        double priceSurcharge = calculatesPriceOfSurcharge(bookDetails, productSwitcher.getKgSurcharge(), getTarif());
        rates.put("surcharge", priceSurcharge);
        Double weight = (Double) weights.get("betterWeight");
        double price = 0;
        if(weight <= area.getMinimumKg()) {
        	price = area.getTarif().doubleValue();
        }else if(weight > area.getMinimumKg()) {
        	price = this.tarif * weight;
        	if(productSwitcher.getIsNextrate()==true) {
        		Integer divWeight = weight.intValue() - area.getMinimumKg();
            	price = area.getTarif().doubleValue() + calculatesPrice(Double.valueOf(divWeight), area.getNextRate().doubleValue());
        	}


        }
//        double price = calculatesPrice((Double) weights.get("betterWeight"), tarif);
        rates.put("price", price);

        double extraCharge = calculatesExtraCharge(isPacking,HARGA_PACKING,bookDetails.size());
        rates.put("exCharge", extraCharge);

        double grandTotal = price + priceInsurance + priceSurcharge + pricePacking ;
        rates.put("total", grandTotal);

        return rates;
    }

    public double calculatesPrice(Double weight, Double tarif2) {
		// TODO Auto-generated method stub
		return weight * tarif2;
	}


	public Map mapAllWeights(BookRequest request, List<DetailBooking> bookDetails, Double pembulatan) {
        Map<String, Double> weight = new HashMap<>();

        double weightPacking = 0;
        if (!request.getTotalPackingPrice().isEmpty()
        		&& !request.getTotalPackingPrice().equals("0")) {
            weightPacking = weightOfPacking(bookDetails);
        }
        weight.put("packing", weightPacking);

        double weightGross = weightOfGross(bookDetails);


        double weightVolume = weightOfVolume(bookDetails);
        double tailVol = weightVolume % 1;
        double tailWeight = weightGross % 1;
        weightVolume = checkPembulatan(pembulatan, weightVolume, tailVol);
        weightGross = checkPembulatan(pembulatan, weightGross, tailWeight);
        weight.put("gross", weightGross);
        weight.put("volume", weightVolume);

        double betterWeight = (weightGross>weightVolume)?weightGross:weightVolume;//weightGraterVolAndGross(bookDetails);
        weight.put("betterWeight", betterWeight);

        return weight;
    }
	private double checkPembulatan(Double pembulatan, double weightVolume, double tail) {
		if(tail >= pembulatan) {
        	weightVolume = Math.ceil(weightVolume);
        }else {
        	weightVolume = Math.floor(weightVolume);
        }
		return weightVolume;
	}
    public Map mapAllWeights(Boolean isPacking, List<DetailBooking> bookDetails, Double pembulatan) {
        Map<String, Double> weight = new HashMap<>();

        double weightPacking = 0;
        if (isPacking) {
            weightPacking = weightOfPacking(bookDetails);
        }
        weight.put("packing", weightPacking);

        double weightGross = weightOfGross(bookDetails);
        double tailWeight = weightGross % 1;
        weightGross = checkPembulatan(pembulatan, weightGross, tailWeight);
        weight.put("gross", weightGross);

        double weightVolume = weightOfVolume(bookDetails);
        double tailVol = weightVolume % 1;
        weightVolume = checkPembulatan(pembulatan, weightVolume, tailVol);

        weight.put("volume", weightVolume);

        double betterWeight = (weightGross>weightVolume)?weightGross:weightVolume;//weightGraterVolAndGross(bookDetails);
        weight.put("betterWeight", betterWeight);

        return weight;
    }
    private List<TBookEntity> initializeBooks(List<DetailBooking> details, TPaymentEntity payment) {
        return details.stream().map(v -> convertModelToBook(v, payment)).collect(Collectors.toList());
    }

    private TBookEntity convertModelToBook(DetailBooking detail, TPaymentEntity payment) {
    	if(payment.getVolume()==0) {
    		detail.setVolume(detail.getGrossWeight());
    		float div = (Float.valueOf(detail.getVolume().toString()) * payment.getProductSwCode().getPembagiVolume().floatValue());
    		div = (float) Math.cbrt(div);
    		detail.setHeight(Math.floor(div));
    		detail.setWidth(Math.floor(div));
    		detail.setLength(Math.floor(div));
    	}

        return TBookEntity.builder()
                .grossWeight(String.valueOf(detail.getGrossWeight().intValue()))
                .height(String.valueOf(detail.getHeight().intValue()))
                .length(String.valueOf(detail.getLength().intValue()))
                .width(String.valueOf(detail.getWidth().intValue()))
                .tglSystem(LocalDateTime.now())
                .bookingCode(payment.getBookingCode())
                .volWeight(String.valueOf(detail.getVolume().intValue()))
                .build();
    }

    private TPaymentEntity initializePayment(BookRequest request, String bookingCode, Map rates, List<DetailBooking> details,
                                           MProductSwitcherEntity productSwitcher, MPickupTimeEntity pickupTime, MGoodsEntity commodity, Map weights, String device) {

        double hpp = calculateHpp(productSwitcher.getProductSwCode(), rates, (Double) rates.get("price"));
        long profit = calculateProfit(hpp, rates);

        String stt = "-";
        if(productSwitcher.getSwitcherEntity().getSwitcherCode() == 309) {
			stt = DateTimeUtil.getDateTime("yy")+"KHA0000"+DateTimeUtil.getDateTime("MM")+bookingCode.substring(1);
		}
        if(request.getReceiverTelp().length() < 9 ) {
        	throw new InternalServerException("Telepon Penerima minimal 10 Digit !");
        }else if(!request.getReceiverTelp().matches("[0-9]+")) {
        	throw new InternalServerException("Telepon Penerima harus Nomor !");
        }
        return TPaymentEntity.builder()
                .bookingCode(bookingCode)
                .userId(userRepo.getOne(request.getUserId()))
                .trxDate(LocalDate.now())
                .trxTime(CommonConstant.dateFormatter("HHmm", LocalDateTime.now()))
                .productSwCode(productSwitcher)
                .senderTelp(request.getSenderTelp())
                .receiverTelp(request.getReceiverTelp())
                .amount(new BigDecimal((Double)rates.get("total")))
                .resi("-")
                .cacode("000001")
                .rc("00")
                .adminTrx(new BigDecimal(0))
                .feeAdmin(new BigDecimal(0))
                .minWeight(request.getMinWeight())
                .feeInternal(new BigDecimal(0))
                .shippingSurcharge(new BigDecimal((Double)rates.get("surcharge")))
                .jumlahLembar(details.size())
                .productDstCode(request.getProductCode().toString())
                .status(PaymentEnum.getPaymentEnum(Integer.valueOf(request.getPayType())).getCode())
                .stt(stt)
                .insurance(new BigDecimal(rates.get("insurance").toString()))
                .extraCharge(new BigDecimal(rates.get("exCharge").toString())) //Rp
                .priceGoods(new BigDecimal(Optional.ofNullable(request.getPriceGoods()).orElse("0")))
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .grossWeight(Math.round((Double)weights.get("gross")))
                .volume(Math.round((Double)weights.get("volume")))
                .comodity(commodity.getGoodsName())
                .goodsId(commodity.getGoodsId().intValue())
                .note(request.getNote().isEmpty() ? "-" : request.getNote())
                .goodsDesc((request.getGoodsDescription().trim().isEmpty())?commodity.getGoodsName():request.getGoodsDescription())
                .pickupAddrId(pickupAddressRepo.getOne(Integer.valueOf(request.getPickupId())))
                .priceKg(new BigDecimal(getTarif()))
                .idPostalCode(mPostalCode.getOne(Integer.valueOf(request.getReceiverPostalCode())))
                .senderName(request.getSenderName())
                .senderAddress(request.getSenderAddress())
                .receiverName(request.getReceiverName())
                .receiverAddress(request.getReceiverAddress())
                .price(new BigDecimal((Double)rates.get("price")))
                .senderEmail(request.getSenderEmail())
                .receiverEmail(request.getReceiverEmail())
                .totalPackKg(((double) weights.get("packing")))
                .totalHpp(new BigDecimal(hpp))
                .profit(new BigDecimal(profit))
                .pickupTimeId(mastPickupTimeRepo.findById(Integer.valueOf(request.getIdPickupTime())).get())
                .pickupDate(getPickupDate(LocalDate.parse(request.getPickupDate())))
                .pickupTime(pickupTime.getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +" - "+ pickupTime.getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .bankDepCode("0")
                .priceRepack(new BigDecimal(0))
                .serviceType(OwnManager.serviceType(request.getServiceType()))
                .trxServer(new Timestamp(Instant.now().toEpochMilli()))
                .qrcodeDate(LocalDate.now())
                .qrcode(getQRCodePay(bookingCode))
                .qrcodeExt(request.getQrcodeExt())
                .statusPay(StatusPayEnum.NOT_PAID.getCode())
                .discountValue(new BigDecimal("0"))
                .amountDiff(new BigDecimal("0"))
                .insufficientFund(new BigDecimal("0"))
                .countPawoon(0)
                .device(device)
                .build();
    }

    private TPaymentEntity initializePayment(CompleteBookReq request, String bookingCode, Map rates, List<DetailBooking> details,
                                             MProductSwitcherEntity productSwitcher, MPickupTimeEntity pickupTime,
                                             MGoodsEntity commodity, Map weights,
                                             TPickupOrderRequestDetailEntity orderRequestEntity,
                                             TAreaEntity area) {

    	Integer status = 0;
        double hpp = calculateHpp(productSwitcher.getProductSwCode(), rates, (Double) rates.get("price"));
        long profit = calculateProfit(hpp, rates);
        MPostalCodeEntity postalCodeEntity=postalCodeRepo.getOne(request.getIdPostalCode());
        if(orderRequestEntity.getIsPay().equals(STATUS_BAYAR)) {
        	if(orderRequestEntity.getAmount().compareTo(new BigDecimal((Double)rates.get("total"))) < 0) {
        		status = PaymentEnum.HOLD_BY_ADMIN.getCode();
            }else {
            	status = PaymentEnum.FINISH_INPUT_AND_PAID.getCode();
            }
        } else {
        	status = PaymentEnum.UNPAID_RECEIVE.getCode();
        }
        if(orderRequestEntity.getOrderRequestEntity().getPickupAddressEntity() == null) {
        	MOfficeEntity office = mOfficeRepo.findByOfficeCode(request.getOfficeCode());
        	orderRequestEntity.getOrderRequestEntity().setPickupAddressEntity(office.getPickupAddrId());
        }
        String stt = "-";
        if(productSwitcher.getSwitcherEntity().getSwitcherCode() == 309) {
			stt = DateTimeUtil.getDateTime("yy")+"KHA0000"+DateTimeUtil.getDateTime("MM")+bookingCode.substring(1);
		}
        if(request.getReceiverPhoneNumber().length() < 9 ) {
        	throw new InternalServerException("Telepon Penerima minimal 10 Digit !");
        }else if(!request.getReceiverPhoneNumber().matches("[0-9]+")) {
        	throw new InternalServerException("Telepon Penerima harus Nomor !");
        }
        return TPaymentEntity.builder()
                .bookingCode(bookingCode)
                .userId(orderRequestEntity.getOrderRequestEntity().getUserEntity())
                .trxDate(LocalDate.now())
                .trxTime(CommonConstant.dateFormatter("HHmm", LocalDateTime.now()))
                .productSwCode(productSwitcher)
                .senderTelp(request.getSenderPhoneNumber())
                .receiverTelp(request.getReceiverPhoneNumber())
                .amount(new BigDecimal((Double)rates.get("total")))
                .resi("-")
                .cacode("000001")
                .rc("00")
                .adminTrx(new BigDecimal(0))
                .feeAdmin(new BigDecimal(0))
                .minWeight(0)
                .feeInternal(new BigDecimal(0))
                .shippingSurcharge(new BigDecimal((Double)rates.get("surcharge")))
                .jumlahLembar(details.size())
                .productDstCode(request.getProductCode().toString())
                .status(status)
                .stt(stt)
                .insurance(new BigDecimal(rates.get("insurance").toString()))
                .extraCharge(new BigDecimal(rates.get("exCharge").toString())) //Rp
                .priceGoods(new BigDecimal(Optional.ofNullable(request.getGoodsPrice()).orElse("0")))
                .origin(mAreaRepo.findByKotaEntityAreaKotaId(request.getOriginId()).getAreaName())
                .destination(postalCodeEntity.getKecamatanEntity().getKecamatan().concat(", ").concat(postalCodeEntity.getKecamatanEntity().getKotaEntity().getName()))
                .grossWeight(Math.round((Double)weights.get("gross")))
                .volume(Math.round((Double)weights.get("volume")))
                .comodity(commodity.getGoodsName())
                .goodsId(commodity.getGoodsId().intValue())
                .note(request.getInstrutionSend()==null ? "-" : request.getInstrutionSend())
                .goodsDesc(request.getDescription())
                .pickupAddrId(orderRequestEntity.getOrderRequestEntity().getPickupAddressEntity())
                .priceKg(new BigDecimal(getTarif()))
                .idPostalCode(mPostalCode.getOne(request.getIdPostalCode()))
                .senderName(request.getSenderName())
                .senderAddress(request.getSenderAddress())
                .receiverName(request.getReceiverName())
                .receiverAddress(request.getReceiverAddress())
                .price(new BigDecimal((Double)rates.get("price")))
                .senderEmail(request.getSenderEmail())
                .receiverEmail(request.getReceiverEmail())
                .totalPackKg(((double) weights.get("packing")))
                .totalHpp(new BigDecimal(hpp))
                .profit(new BigDecimal(profit))
                .pickupTimeId(orderRequestEntity.getOrderRequestEntity().getPickupTimeEntity())
                .pickupDate(getPickupDate(orderRequestEntity.getOrderRequestEntity().getOrderDate()))
                .pickupTime(pickupTime.getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +" - "+ pickupTime.getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .bankDepCode("0")
                .priceRepack(new BigDecimal(0))
                .serviceType(OwnManager.serviceType("0"))
                .trxServer(new Timestamp(Instant.now().toEpochMilli()))
                .qrcodeDate(LocalDate.now())
                .qrcode(getQRCodePay(bookingCode))
                .qrcodeExt(request.getQrCode())
                .statusPay(StatusPayEnum.NOT_PAID.getCode())
                .discountValue(new BigDecimal("0"))
                .amountDiff(new BigDecimal("0"))
                .insufficientFund(new BigDecimal("0"))
                .device(DeviceEnum.WEB.getCodeString())
//                .tbookleadtime(TBookLeadTime.builder()
//                		.bookingCode(bookingCode)
//                		.startDay(area.getStartDay())
//                		.endDay(area.getEndDay())
//                		.build())
                .build();
    }

    public THistoryBookEntity initializeHistoryBook(String bookingCode, Integer size, String userId,String remarks,String stt) {
        return THistoryBookEntity.builder()
                .bookingCode(bookingCode)
                .stt(stt)
                .remarks(remarks)
                .piece(String.valueOf(size))
                .trxDate(LocalDateTime.now())
                .issuedBy(userId)
                .updatedBy(userId)
                .build();
    }

    public TMutasiEntity insertMutasi(TPaymentEntity payment,String trxNo,String description,MutasiEnum type) {
    	BigDecimal saldo = new BigDecimal(getSaldoMutasiByUserId(payment.getUserId()));
    	BigDecimal amount = payment.getAmount();
    	if(type.equals(MutasiEnum.REFUND)) {
    		amount = amount.multiply(new BigDecimal(-1));
    	}
    	saldo = saldo.add(amount);
    	if(saldo.compareTo(BigDecimal.ZERO) > 0 && payment.getUserId().getDepositType().equals(DepositTypeEnum.DEPOSIT.getValue())) {
    		throw new NotFoundException("Saldo Tidak Cukup !");
    	}
    	return TMutasiEntity.builder()
    			.trxNo(trxNo)
    			.amount(amount)
    			.customerId(payment.getUserId().getUserId())
    			.productSwCode(payment.getProductSwCode().getProductSwCode().toString())
    			.descr(description)
    			.saldo(saldo)
    			.trxType(type.getCode())
    			.trxDate(LocalDate.now())
    			.trxTime(LocalTime.now())
    			.userId(payment.getUserId())
    			.updateBy(payment.getUserId().getUserId())
    			.trxServer(LocalDateTime.now())
    			.build();
    }
    public MUserEntity updateBalanceUser(String userId, double balance) {
        MUserEntity user = getUserById(userId);
        user.setBalance(new BigDecimal(balance));
        return user;
    }

    private void forUpdate(MUserEntity user, TMutasiEntity mutasi) {
        double balance = user.getBalance().doubleValue() * -1;

        if (balance < 0) {

        } else {

        }
    }


    private double weightOfGross(List<DetailBooking> details) {
        if (details.size() != 0) {
            return details.stream().mapToDouble(DetailBooking::getGrossWeight).sum();
        }

        return 0;
    }

    private double weightGraterVolAndGross(List<DetailBooking> details) {
        return details.stream().mapToDouble(v -> Math.max(v.getGrossWeight(), v.getVolume())).sum();
    }

    private double weightOfVolume(List<DetailBooking> details) {
        if (details.size() != 0) {
            return details.stream().mapToDouble(DetailBooking::getVolume).sum();
        }

        return 0;
    }

    private double weightOfPacking(List<DetailBooking> details) {
        if (details.size() != 0) {
            return details.stream()
                    .mapToDouble(v -> CommonConstant.greaterThan(v.getLength(), v.getHeight(), v.getWidth()))
                    .map(greater -> Math.ceil(greater / PEMBAGI_PACKING))
                    .sum();
        }

        return 0;
    }

    private double calculateHpp(Long productId, Map rates, double price) {
        MProductSwitcherEntity productSwitcher = getProductById(productId);
        double insurance = (Double) rates.get("insurance");
        double hpp = price + insurance;

        double commission = hpp * productSwitcher.getKomisi() / 100;
        return hpp - commission;
    }

    private long calculateProfit(double hpp, Map rates) {
        double extraCharge = (Double) rates.get("exCharge");
        double totalPrice = (Double) rates.get("total");

        double profit = totalPrice - hpp - extraCharge;
        return Math.round(profit);
    }

	public double calculatesExtraCharge(boolean isPack,int extraCharge, double koli) {
		if(isPack==false) return 0;
        return extraCharge * koli;
    }

    private double calculatesPrice(List<DetailBooking> details, double price) {
        double sum = details.stream().mapToDouble(v -> Math.max(v.getGrossWeight(), v.getVolume())).sum();
        return sum * price;
    }

    private double calculatesPriceOfSurcharge(List<DetailBooking> details, double surcharge, double price) {
        double x = details.stream().mapToDouble(v -> Math.max(v.getGrossWeight(), v.getVolume())).sum();
        if (x > surcharge & surcharge > 0) {
            return x * price / 2;
        } else {
            return 0;
        }
    }

    public double calculatesPriceOfInsurance(double premiAssurance, double nilaiBarang) {
        double fee = (premiAssurance/100) * nilaiBarang;

        return fee > INSURANCE_MINIMUM ? fee : INSURANCE_MINIMUM;
    }

    /**
     * ONE of the greatest values of (length x width x height) / pembagiPackingKayu = i KG
     * @return (i KG x price) + (HARGA_PACKING x total item) in Rp
    **/
    private double calculatesPriceOfPacking(List<DetailBooking> details, Double price) {
        if (details.size() != 0) {
            long sum = details
                    .stream()
                    .mapToDouble(v -> CommonConstant.greaterThan(v.getLength(), v.getHeight(), v.getWidth()))
                    .mapToLong(greater -> Long.valueOf((long) Math.ceil(greater / PEMBAGI_PACKING)))
                    .sum();

            long packingRates = HARGA_PACKING * details.size();
            return (sum * price) + packingRates;
        }

        return 0;
    }


    /**
     * (length x width x height) / pembagivolume = KG
    **/
    private List<DetailBooking> recalculateDetailBooking(List<DetailBooking> bookDetails, Double pembagiVolume) {
        List<DetailBooking> details = new ArrayList<>();
        bookDetails.forEach(v -> {
            double vol = (Double.valueOf(v.getHeight()) * Double.valueOf(v.getLength()) * Double.valueOf(v.getWidth())) / pembagiVolume;
            details.add(new DetailBooking(vol, v.getGrossWeight(), v.getLength(), v.getWidth(), v.getHeight()));
        });

        return details;
    }

    public double calculateSaldo(double grandTotal, String userId) {
        double saldo = 0;
        MUserEntity user = getUserById(userId);


//        if (user.getDepositType().equals(DepositType.CREDIT.getValue()) && creditValidated < 0) {
//        	
//            return saldo;
//        }
        if (user.getDepositType().equals(DepositTypeEnum.CREDIT.getValue())) {
        	int creditValidated = checkValidateCredit(user);
        	if(creditValidated < 0) return saldo;
            double temp = (user.getBalance().doubleValue() * -1) - grandTotal;
            saldo = Math.abs(temp);
        } else {
            saldo = (user.getBalance().doubleValue() * -1) - grandTotal;
        }

        return saldo;
    }

    public int checkValidateCredit(MUserEntity user) {
        long credit = 0;
        TCreditEntity transCredit = getCreditById(user.getUserId());
        if (transCredit != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate tglMulai = LocalDate.parse(transCredit.getTglMulai(), dateFormatter);
            long inv = LocalDate.now().toEpochDay() - tglMulai.toEpochDay();
            long sel = TimeUnit.DAYS.convert(inv, TimeUnit.DAYS);
            credit = Long.valueOf(user.getCreditDay()) - sel;
        }

        return (int) credit;
    }

    private TCreditEntity getCreditById(String userId) {
        List<TCreditEntity> credits = transCreditRepo.findByUserAndNominalGraterZero(userId, FLAG);
        if(credits.isEmpty()) {
            return null;
        }

        return credits.get(0);
    }

    private MUserEntity getUserById(String userId) {
        return mastUserRepo.findById(userId).orElseThrow(() -> new NotFoundException("user not found"));
    }

    private MProductSwitcherEntity getProductById(Long productId) {
        return mastProductSwitcherRepo.findById(productId).orElseThrow(() -> new NotFoundException("product not found"));
    }

    private MGoodsEntity getCommodity(Long commodityId) {
        return mastGoodsRepo.findById(commodityId).orElseThrow(() -> new NotFoundException("commodity not found"));
    }

    private MPickupTimeEntity getPickupTimeById(Integer pickupTimeId) {
        return mastPickupTimeRepo.findById(pickupTimeId).orElseThrow(() -> new NotFoundException("pickup time not found"));
    }

    public Double getSaldoMutasiByUserId(MUserEntity userId) {
        double balance = 0;
        List<TMutasiEntity> mutate = transMutasiRepo.findTopByUserIdOrderByCounterMutasiDesc(userId);
        if (mutate.isEmpty()) {
            return balance;
        } else {
            return mutate.stream().findFirst().get().getSaldo().doubleValue();
        }
    }

    private boolean checkSenderAndSave(BookRequest request) {

    	boolean isSave = false;
    	if("1".equals(request.getSenderSave()))isSave=true;
        if (isSave) {
            MSenderEntity sender = MSenderEntity.builder()
                    .senderName(request.getSenderName())
                    .senderAddress(request.getSenderAddress())
                    .senderEmail(request.getSenderEmail().isEmpty() ? null : request.getSenderEmail())
                    .senderTelp(request.getSenderTelp())
                    .userId(userRepo.getOne(request.getUserId()))
                    .createdBy(request.getUserId())
                    .createdDate(LocalDate.now())
                    .updatedBy(request.getUserId())
                    .updatedDate(LocalDate.now())
                    .status(ACTIVE)
                    .build();

            mastSenderRepo.save(sender);
            return true;
        }
        return false;
    }

    private boolean checkReceiverAndSave(BookRequest request) {
    	boolean isSave = false;
    	if("1".equals(request.getReceiverSave()))isSave=true;
        if (isSave) {
            MReceiverEntity receiver = MReceiverEntity.builder()
                    .receiverName(request.getReceiverName())
                    .receiverAddress(request.getReceiverAddress())
                    .receiverTelp(request.getReceiverTelp())
                    .receiverEmail(request.getReceiverEmail().isEmpty() ? null : request.getReceiverEmail())
                    .idPostalCode(postalCodeRepo.getOne(Integer.valueOf(request.getReceiverPostalCode())))
                    .userId(userRepo.getOne(request.getUserId()))
                    .createdBy(request.getUserId())
                    .createdDate(LocalDate.now())
                    .updatedBy(request.getUserId())
                    .updatedDate(LocalDate.now())
                    .status(ACTIVE)
                    .build();

            mastReceiverRepo.save(receiver);
            return true;
        }
        return false;
    }

    private String generateBookingCode() {
        TPaymentEntity transPayment = transPaymentRepo.findTopByOrderByBookingCodeDesc();

        String bookCode = transPayment.getBookingCode();
        String counter = bookCode.substring(1, 5);
        char h = bookCode.charAt(0);

        if(counter.equals("9999")) {
            int ascii = h;
            ascii++;
            h = (char) ascii;
            bookCode = h + "0001";
        } else {
            int count = Integer.valueOf(counter) + 1;
            counter = counter.substring(0, counter.length() - String.valueOf(count).length());
            counter += String.valueOf(count);
            bookCode = h + counter;
        }

        return bookCode;
    }

    public String getQRCodePay(String kodebook) {
		String random = String.valueOf((long) (Math.random() * Math.pow(10, 2)));
		String digit = UniqueRandom.calculateCheckDigit(random);
		String qrcode = random + digit + kodebook;
		return qrcode;
	}

    public LocalDate getPickupDate(LocalDate date) {
    	Boolean isFree = true;
    	String day = "";
    	int isActive = 1;
    	while(isFree) {
    		day = DateTimeUtil.getNameDay(date);
    		MFreedateEntity freeDate = mFreedateRepo.findByTahunAndBulanAndTgl(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    		MFreedayEntity freeDay = mFreedayRepo.findByDayNameIgnoreCaseContainingAndIsActive(day, isActive);
    		if(freeDate != null || freeDay != null) {
    			isFree = true;
    			date = date.plusDays(1L);
    		}else {
    			isFree = false;
    		}
    	}
    	return date;
    }


    private PaymentEnum getStatusPayment(TPaymentEntity pay,PaymentEnum paymentEnum) {
    	PaymentEnum payStat = paymentEnum;
    	if(payStat.equals(PaymentEnum.REQUEST)) {
    		if(pay.getStatus().equals(PaymentEnum.HOLD_BY_ADMIN.getCode())
    				|| pay.getStatus().equals(PaymentEnum.UNPAID_RECEIVE.getCode())) {
    			payStat=PaymentEnum.FINISH_INPUT_AND_PAID;
    		}else if (pay.getStatus().equals(PaymentEnum.HOLD_BY_WAREHOUSE.getCode())) {
    			payStat=PaymentEnum.ACCEPT_IN_WAREHOUSE;
    		}else if(pay.getStatus().equals(PaymentEnum.RECEIVE_IN_COUNTER.getCode())) {
    			payStat=PaymentEnum.RECEIVE_IN_COUNTER;
    		}
    	}else {
    		if(pay.getStatus().equals(PaymentEnum.HOLD_BY_ADMIN.getCode())) {
    			payStat=PaymentEnum.HOLD_BY_ADMIN;
    		}else if (pay.getStatus().equals(PaymentEnum.HOLD_BY_WAREHOUSE.getCode())) {
    			payStat=PaymentEnum.HOLD_BY_WAREHOUSE;
    		}else if (pay.getStatus().equals(PaymentEnum.UNPAID_RECEIVE.getCode())) {
    			payStat = PaymentEnum.UNPAID_RECEIVE;
    		}
    	}

    	return payStat;
    }
    public TPaymentEntity insertWarehouseBarangTitipan(TPaymentEntity payment, MUserEntity user, PaymentEnum payStat) {
        payment.setStatus(payStat.getCode());
			String office = officeRepo.findByUserIdUserId(user.getUserId()).stream().findFirst().get().getOfficeCode().getOfficeCode();
        String kode = getCounterWarehouse(office);
        payment.setOfficeCode(office);
        if(isInsertWarehouseTitipan(kode,payment,user)) {
            if(payment.getStatus().equals(PaymentEnum.OUTGOING_BY_COUNTER.getCode())) {
                payment.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
            }else {
                payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
            }
        }
        return payment;
    }
    public TPaymentEntity insertWarehouseBarangTitipan(TPaymentEntity payment, MUserEntity user, PaymentEnum payStat,String officeCode) {
			payment.setStatus(payStat.getCode());
//			String office = officeRepo.findByUserId(user.getUserId()).stream().findFirst().get().getOfficeCode().getOfficeCode();
			String kode = getCounterWarehouse(officeCode);
			payment.setOfficeCode(officeCode);
			if(isInsertWarehouseTitipan(kode,payment,user)) {
				if(payment.getStatus().equals(PaymentEnum.OUTGOING_BY_COUNTER.getCode())) {
					payment.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
				}else {
					payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
				}
			}
		return payment;
	}
    public Boolean isInsertWarehouseTitipan(String kode,TPaymentEntity pay,MUserEntity user) {
		// TODO Auto-generated method stub
    	Integer status = WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode();
    	if(pay.getStatus().equals(PaymentEnum.OUTGOING_BY_COUNTER.getCode())) {
    		status = WarehouseEnum.APPROVE.getCode();
    	}
		TWarehouseReceiveEntity whReceive = TWarehouseReceiveEntity.builder()
				.code(kode)
				.createBy(user.getUserId())
				.createDate(LocalDateTime.now())
				.officeCode(pay.getOfficeCode())
				.build();
//		warehouseRepo.save(whReceive);
		TWarehouseReceiveDetailEntity whReceiveDetail = TWarehouseReceiveDetailEntity
				.builder()
				.warehouseReceiveId(whReceive)
				.bookId(pay)
				.status(status)
				.createBy(user.getUserId())
				.createAt(LocalDateTime.now())
				.build();
		warehouseDetailRepo.save(whReceiveDetail);

		return true;

	}

    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public SaveResponse updateBookingCode(String qrcode,String bookingCode,String userId) {

    	TOfficeEntity office = officeRepo.findByUserIdUserId(userId).stream().findAny().get();
    	TPickupOrderRequestDetailEntity orderReq = orderRequestDetailRepo.findByQrCodeOrQrcodeExt(qrcode,qrcode);
    	if(orderReq == null)throw new NotFoundException("Pesanan Tidak Ditemukan !");
    	TPaymentEntity payment = paymentService.getBookAndStatusAndUserId(bookingCode,PaymentEnum.REQUEST.getCode(),orderReq.getOrderRequestEntity().getUserEntity().getUserId());
    	if(payment==null) throw new NotFoundException("Pesanan Tidak Ditemukan !");
    	if(payment.getStatus().equals(PaymentEnum.PENDING.getCode())) {
    		throw new NotFoundException("Pesanan Belum Dibayar !");
    	}
    	TPaymentEntity oldPayment = paymentService.createOldPayment(payment);
    	payment.setStatus(PaymentEnum.FINISH_INPUT_AND_PAID.getCode());
    	payment.setQrcodeExt(orderReq.getQrcodeExt());
    	payment.setOfficeCode(office.getOfficeCode().getOfficeCode());

    	orderReq.setBookCode(payment.getBookingCode());
    	orderReq.setStatus(RequestPickupEnum.FINISH_BOOK.getValue());
    	orderReq.setUpdateBy(userId);
    	orderReq.setUpdateDate(LocalDateTime.now());
    	//add Ibnu at 30/06/2020
    	orderReq.setNamaPenerima(payment.getReceiverName());
    	orderReq.setProductSwitcherEntity(payment.getProductSwCode());
    	orderReq.setAreaId(payment.getIdPostalCode().getKecamatanEntity());
    	orderReq.setWeight(payment.getGrossWeight().doubleValue());
    	orderReq.setIdPayment(payment.getIdPayment());
    	orderReq.setAmount(payment.getAmount());
    	orderReq.setPaymentOption(payment.getPaymentOption());
    	orderReq.setCountPawoon(payment.getCountPawoon());
    	//end
    	paymentService.saveAndFlush(payment);
    	orderRequestDetailRepo.saveAndFlush(orderReq);
    	historyTransactionService.createHistory(oldPayment, payment, userId);

    	TWarehouseReceiveDetailEntity warehouseReceiveDetailEntity = warehouseDetailRepo
    			.findByQrcodeRequestAndStatus(qrcode,WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode())
    			.orElseThrow(()-> new NotFoundException("QR Code Tidak Ditemukan"));
    	warehouseReceiveDetailEntity.setBookId(payment);
    	warehouseDetailRepo.save(warehouseReceiveDetailEntity);
    	return SaveResponse.builder()
    			.saveInformation("Berhasil Update Barang Titipan")
    			.saveStatus(1)
    			.build();
    }

    @Transactional
    public SaveResponse completeBookingReqAll(List<CompleteBookReq> allreq,String userLogin) {
    	List<RespUncomplete> lsResp = new ArrayList<>();
    	for(CompleteBookReq req : allreq) {
    		lsResp.addAll(complateBookingReq(req,userLogin).getResults());
    	}
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil")
    			.results(lsResp)
    			.build();
    }

    public void insertHpp(TPaymentEntity pay) {
    	THppPaymentEntity hpp = hppRepo.findByBookingCode(pay)
    			.orElse(THppPaymentEntity.builder()
    					.bookingCode(pay)
    					.hpp(pay.getTotalHpp())
    					.hppActual(new BigDecimal("0"))
    					.lastUpdate(LocalDateTime.now())
    					.build());
    	hpp.setHpp(pay.getTotalHpp());
    	hppRepo.save(hpp);

    }

    private Boolean createHistoryPickupRequest(TPaymentEntity payment,TPickupOrderRequestDetailEntity pickupDetail) {
    	List<TPickupOrderHistoryEntity> lPickupHis = tPickupHistory.findAllByPickupOrderDetailId(pickupDetail.getSeq());
    	if(lPickupHis.size() > 0) {
    		lPickupHis.addAll(tPickupHistory.findAllByPickupOrderIdAndPickupOrderDetailId(lPickupHis.get(0).getPickupOrderId(), null));
    		Collections.sort(lPickupHis, (a,b)->a.getCreatedDate().compareTo(b.getCreatedDate()));
    		for(TPickupOrderHistoryEntity pickH : lPickupHis) {
    			TPaymentHistoryEntity payHistory = TPaymentHistoryEntity
    		            .builder()
    		            .bookingCode(payment)
    		            .userId(pickH.getCreatedBy())
    		            .amount(payment.getAmount())
    		            .lastAmount(payment.getAmount())
    		            .trxServer(payment.getTrxServer())
    		            .jumlahLembar(payment.getJumlahLembar())
    		            .insurance(payment.getInsurance())
    		            .extraCharge(payment.getExtraCharge())
    		            .price(payment.getPrice())
    		            .priceKg(payment.getPriceKg())
    		            .priceRepack(payment.getPriceRepack())
    		            .lastPrice(payment.getPrice())
    		            .lastPriceKg(payment.getPriceKg())
    		            .lastPriceRepack(payment.getPriceRepack())
    		            .grossWeight(payment.getGrossWeight())
    		            .lastGrossWeight(payment.getGrossWeight())
    		            .volume(payment.getVolume())
    		            .lastVolume(payment.getVolume())
    		            .status(RequestPickupEnum.toPaymentEnum(pickH.getStatus()).getValue())
    		            .lastStatus(RequestPickupEnum.toPaymentEnum(pickH.getLastStatus()).getValue())
    		            .lastUser(pickH.getCreatedBy())
    		            .lastUpdate(pickH.getCreatedDate())
    		            .reason(pickH.getReason()==null?null:pickH.getReason())
    		            .build();
    			tPaymentHistoryRepo.save(payHistory);
    		}
    	}
    	return true;
    }
}

