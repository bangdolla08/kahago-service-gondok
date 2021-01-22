package com.kahago.kahagoservice.service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.kahago.kahagoservice.client.FeignService;
import com.kahago.kahagoservice.client.model.request.ReqTrackingLP;
import com.kahago.kahagoservice.client.model.response.ResTracking;
import com.kahago.kahagoservice.client.model.response.ResTrackingLP;
import com.kahago.kahagoservice.client.model.response.Tracking;
import com.kahago.kahagoservice.client.model.response.TrackingLP;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.THistoryBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.TrackRes;
import com.kahago.kahagoservice.model.response.TrackingInternalResponse;
import com.kahago.kahagoservice.model.response.TrackingPCP;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Service
@Slf4j
public class TrackingService {

    public static final String PREFIX_PATH_IMAGE = "images/vendor/";
    private static final String ACTION = "track";
    private static final Integer ID_VENDOR_LION_PARCEL = 301;
    private static final Integer ID_VENDOR_PCP = 306;
    private static final Integer ID_VENDOR_BUKASEND=314;

    @Autowired
    private TPaymentRepo transPaymentRepo;

    @Autowired
    private THistoryBookRepo transHistoryBookRepo;

    @Autowired
    private FeignService feignService;

    @Autowired
    private MSwitcherRepo mastSwitcherRepo;
    
    @Autowired
    private TPaymentHistoryRepo tHistoryRepo;
    
    @Autowired
    private MUserRepo mUserRepo;
    
    public List<TrackRes> findByIdBookingOrResi(String id) {
        AtomicInteger index = new AtomicInteger(1);

        TPaymentEntity payment = checkPaymentValidExist(id);
        List<TrackRes> resTracks = transHistoryBookRepo.findByBookingCode(payment.getBookingCode())
                .stream()
                .map(v -> convertToTransModel(v, index.getAndIncrement()))
                .collect(Collectors.toList());
        Map<String, Object> maps = new HashMap<>();
        maps.put("url", payment.getProductSwCode().getSwitcherEntity().getVendorProperties().get(0).getUrl());
        maps.put("vendorName", payment.getProductSwCode().getSwitcherEntity().getName());
        maps.put("resi", payment.getStt());
        maps.put("idVendor", payment.getProductSwCode().getSwitcherEntity().getSwitcherCode());
        maps.put("bookingCode", resTracks.get(0).getBookingCode());
        maps.put("images", payment.getProductSwCode().getSwitcherEntity().getImg().substring(payment.getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/") + 1));


        if (payment.getProductSwCode().getSwitcherEntity().getSwitcherCode().equals(ID_VENDOR_LION_PARCEL)) {
            fetchAndTrackingLionParcel(resTracks, maps, index.get());

        }else if(payment.getProductSwCode().getSwitcherEntity().getSwitcherCode().equals(ID_VENDOR_PCP)) {
        	maps.put("piece", resTracks.get(0).getPiece());
        	fetchTrackingPCP(resTracks, maps, index.get());
        }else if(payment.getProductSwCode().getSwitcherEntity().getSwitcherCode().equals(ID_VENDOR_BUKASEND)) {
        	maps.put("piece", resTracks.get(0).getPiece());
        	fetchAndTrackingBukaSend(resTracks, maps, index.get());
        }else {
            maps.put("piece", resTracks.get(0).getPiece());
            fetchAndTracking(resTracks, maps, index.get());

        }

        return resTracks;
    }

    public List<TrackRes> findByResiVendor(String resi, String idVendor) {
        AtomicInteger index = new AtomicInteger(1);

        MSwitcherEntity switcher = checkSwitcherValidExist(idVendor);
        List<TrackRes> resTracks = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        maps.put("url", switcher.getVendorProperties().get(0).getUrl());
        maps.put("vendorName", switcher.getName());
        maps.put("resi", resi);
        maps.put("idVendor", switcher.getSwitcherCode());
        maps.put("bookingCode", "-");
        maps.put("piece", "1");
        maps.put("images", switcher.getImg().substring(switcher.getImg().lastIndexOf("/")+1));

        if (switcher.getSwitcherCode().equals(ID_VENDOR_LION_PARCEL)) {
            fetchAndTrackingLionParcel(resTracks, maps, index.get());

        } else if(switcher.getSwitcherCode().equals(ID_VENDOR_PCP)) {
        	fetchTrackingPCP(resTracks, maps, index.get());
        }else if(switcher.getSwitcherCode().equals(ID_VENDOR_BUKASEND)) {
        	fetchAndTrackingBukaSend(resTracks, maps, index.get());
        }else {
            fetchAndTracking(resTracks, maps, index.get());

        }
        return resTracks;
    }


    private TrackRes convertToTransModel(THistoryBookEntity historyBook, int number) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return TrackRes.builder()
                .sequence(number)
                .tanggal(historyBook.getTrxDate().format(timeFormatter))
                .stt(historyBook.getStt())
                .information(historyBook.getRemarks())
                .piece(historyBook.getPiece())
                .bookingCode(historyBook.getBookingCode())
                .shipmentBy("Kaha Go")
                .updatedBy(historyBook.getUpdatedBy())
                .image(PREFIX_PATH_IMAGE + "kahago.png")
                .build();
    }

    private TPaymentEntity checkPaymentValidExist(String bookingCode) {
         return transPaymentRepo.validateTracking(bookingCode, ACTION).orElseThrow(() -> new NotFoundException("ID Book tidak ditemukan"));
    }

    private void fetchAndTracking(List<TrackRes> resTracks, Map maps, int number) {
        URI uri = URI.create((String) maps.get("url"));
        ResTracking response = feignService.fetchTrackingByResi(uri, (String) maps.get("resi"));
        String informastion = "";
        if(response.getRc() != null && response.getRc().equals("00")) {
        	for (Tracking tracking : response.getTrackHistory()) {
            	informastion="";
            	if(tracking.getReceiverName()!=null) {
            		informastion = tracking.getReceiverName();
            	}
            	if(tracking.getStatus()!=null) {
            		informastion = informastion+" "+tracking.getStatus();
            	}
            	if(tracking.getCity()!=null) {
            		informastion = informastion+" "+tracking.getCity();
            	}
                TrackRes resTrack = TrackRes.builder()
                        .sequence(number)
                        .tanggal(parseTanggal(tracking.getDateTime(),(Integer) maps.get("idVendor")))
                        .stt((String) maps.get("resi"))
                        .information(informastion)
                        .piece((String) maps.get("piece"))
                        .bookingCode((String) maps.get("bookingCode"))
                        .shipmentBy((String) maps.get("vendorName"))
                        .updatedBy((String) maps.get("vendorName"))
                        .image(PREFIX_PATH_IMAGE +maps.get("images"))
                        .build();

                number++;
                resTracks.add(resTrack);
            }       
        }
        
        Comparator<TrackRes> sortTgl = (a,b)->a.getTanggal().compareToIgnoreCase(b.getTanggal());
        Collections.sort(resTracks, sortTgl);
        if (resTracks.size() == 0) {
            throw new NotFoundException("ID Book / Resi tidak ditemukan");
        }
    }

    private MSwitcherEntity checkSwitcherValidExist(String idVendor) {
        return mastSwitcherRepo.validateTracking(Integer.valueOf(idVendor), ACTION).orElseThrow(() -> new NotFoundException("Resi tidak ditemukan"));
    }

    private void fetchAndTrackingLionParcel(List<TrackRes> resTracks, Map maps, int number) {

        URI uri = URI.create((String) maps.get("url"));
        ResTrackingLP response = feignService.fetchTrackByResiLionParcel(uri, new ReqTrackingLP((String) maps.get("resi")));
       if(response.getRc() != null && response.getRc().equals("00")) {
        	for (TrackingLP track : response.getShipmentTrackingLP()) {
                TrackRes resTrack = TrackRes.builder()
                        .sequence(number)
                        .tanggal(parseTanggal(track.getUpdatedOn(),(Integer) maps.get("idVendor")))
                        .stt(track.getSttNumber())
                        .information(track.getRemarks())
                        .piece(track.getPiece())
                        .bookingCode((String) maps.get("bookingCode"))
                        .shipmentBy((String) maps.get("vendorName"))
                        .updatedBy(track.getUpdatedBy())
                        .image(PREFIX_PATH_IMAGE +maps.get("images"))
                        .build();

                number++;
                resTracks.add(resTrack);
            }        }
        
        Comparator<TrackRes> sortTgl = (a,b)->a.getTanggal().compareToIgnoreCase(b.getTanggal());
        Collections.sort(resTracks, sortTgl);
        if (resTracks.size() == 0) {
            throw new NotFoundException("ID Book / Resi tidak ditemukan");
        }
    }
    
    private void fetchTrackingPCP(List<TrackRes> resTracks, Map maps, int number) {
        URI uri = URI.create((String) maps.get("url")+(String) maps.get("resi"));
        try {
        	ResTracking response = feignService.fetchTrackingByResiPCP(uri);        
            String informastion = "";
            if(response !=null && response.getStatus()) {
            	for (TrackingPCP tracking : response.getDetail()) {
            		informastion = tracking.getOfficeName()+" "+tracking.getStatusName()+" "+tracking.getReason();
                    TrackRes resTrack = TrackRes.builder()
                            .sequence(number)
                            .tanggal(parseTanggal(tracking.getTrackingDate(),(Integer) maps.get("idVendor")))
                            .stt((String) maps.get("resi"))
                            .information(informastion)
                            .piece((String) maps.get("piece"))
                            .bookingCode((String) maps.get("bookingCode"))
                            .shipmentBy((String) maps.get("vendorName"))
                            .updatedBy((String) maps.get("vendorName"))
                            .image(PREFIX_PATH_IMAGE +maps.get("images"))
                            .build();

                    number++;
                    resTracks.add(resTrack);
                } 
            }
            if (resTracks.size() == 0) {
                throw new NotFoundException("ID Book / Resi tidak ditemukan");
            }
        }catch (FeignException e) {
			// TODO: handle exception
        	log.error(e.getMessage());
        	Comparator<TrackRes> sortTgl = (a,b)->a.getTanggal().compareToIgnoreCase(b.getTanggal());
        	Collections.sort(resTracks, sortTgl);
		}
        Comparator<TrackRes> sortTgl = (a,b)->a.getTanggal().compareToIgnoreCase(b.getTanggal());
        Collections.sort(resTracks, sortTgl); 
    }
    
    private void fetchAndTrackingBukaSend(List<TrackRes> resTracks, Map maps, int number) {
    	String resi = (String) maps.get("resi");
    	String idresi = resi.substring(0, resi.lastIndexOf("|"));
    	String noresi = resi.substring(resi.lastIndexOf("|")+1);
        URI uri = URI.create((String) maps.get("url")+"/"+idresi);
        ResTracking response = feignService.fetchTrackingByResi(uri, noresi);
        String informastion = "";
        if(response.getRc() != null && response.getRc().equals("200")) {
        	for (Tracking tracking : response.getTrackHistory()) {
            	informastion="";
            	if(tracking.getStatus()!=null) {
            		informastion = informastion+" "+tracking.getStatus();
            	}
            	if(tracking.getBlStatus() != null) {
            		informastion = informastion+" "+tracking.getBlStatus();
            	}
                TrackRes resTrack = TrackRes.builder()
                        .sequence(number)
                        .tanggal(parseTanggal(tracking.getDateTime(),(Integer) maps.get("idVendor")))
                        .stt((String) maps.get("resi"))
                        .information(informastion)
                        .piece((String) maps.get("piece"))
                        .bookingCode((String) maps.get("bookingCode"))
                        .shipmentBy((String) maps.get("vendorName"))
                        .updatedBy((String) maps.get("vendorName"))
                        .image(PREFIX_PATH_IMAGE +maps.get("images"))
                        .build();

                number++;
                resTracks.add(resTrack);
            }       
        }
        
        Comparator<TrackRes> sortTgl = (a,b)->a.getTanggal().compareToIgnoreCase(b.getTanggal());
        Collections.sort(resTracks, sortTgl);
        if (resTracks.size() == 0) {
            throw new NotFoundException("ID Book / Resi tidak ditemukan");
        }
    }
    
    private String parseTanggal(String date,Integer switcherCode) {
    	String result="";
    	LocalDateTime tgl = LocalDateTime.now();
    	try {
    		switch (switcherCode) {
			case 301:
				tgl = DateTimeUtil.getDateFromString(date, "dd-MMM-yyyy HH:mm");
				break;
			case 302:
				tgl = DateTimeUtil.getDateFromString(date, "yyyy-MM-dd HH:mm");
				break;
			case 303:
				tgl = LocalDateTime.parse(date);
				break;
			case 305:
				tgl = DateTimeUtil.getDateFromString(date, "yyyy-MM-dd HH:mm:ss");
				break;
			case 306:
				tgl = DateTimeUtil.getDateFromString(date, "yyyy-MM-dd HH:mm:ss");
				break;
			case 307:
				tgl = DateTimeUtil.getDateFromString(date, "yyyy-MM-dd HH:mm");
				break;
			case 308:
				tgl = DateTimeUtil.getDateFromString(date+" 00:00", "MM/dd/yyyy HH:mm");
				break;
			case 309:
				tgl = DateTimeUtil.getDateFromString(date, "yyyy-MM-dd HH:mm:ss");
				break;
			case 310:
				tgl = DateTimeUtil.getDateFromString(date, "yyyy-MM-dd HH:mm:ss");
				break;
			case 311:
				tgl = DateTimeUtil.getDateFromString(date, "dd-MM-yyyy HH:mm");
				break;
			case 312:
				tgl = DateTimeUtil.getDateFromString(date, "dd-MM-yyyy HH:mm:ss");
				break;
			case 314:
				tgl = ZonedDateTime.parse(date).toLocalDateTime();
				break;
			default:
				break;
			}    		
    	}catch (ParseException e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	result = tgl.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    	return result;
    }
    
    public List<TrackingInternalResponse> getTrackingInternal(String bookingCode){
    	List<TPaymentHistoryEntity> lHistory = tHistoryRepo.findByBookingCodeBookingCodeOrderByLastUpdateAsc(bookingCode);
    	List<TrackingInternalResponse> result = new ArrayList<>();
    	int seq = 1;
    	for(TPaymentHistoryEntity pH : lHistory) {
    		TrackingInternalResponse trackIn = toTracking(pH, seq);
    		result.add(trackIn);
    		seq++;
    	}
    	return result;
    }
    
    private TrackingInternalResponse toTracking(TPaymentHistoryEntity entity,Integer seq) {
    	String tgl = entity.getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    	MUserEntity admin = mUserRepo.getMUserEntitiesBy(entity.getUserId());
    	String adminName = "SYSTEM";
    	if(admin != null) {
    		if(entity.getReason() != null && (entity.getLastStatus().equals(PaymentEnum.ASSIGN_PICKUP.getCode())
    				|| entity.getLastStatus().equals(PaymentEnum.DRAFT_PICKUP.getCode()))) {
        		adminName = admin.getName()+" | "+entity.getReason();
        	}else {
        		adminName = admin.getName();
        	}
    	}
    	
    	return TrackingInternalResponse.builder()
    			.seq(seq)
    			.amount(entity.getLastAmount().toString())
    			.bookingCode(entity.getBookingCode().getBookingCode())
    			.noResi(entity.getBookingCode().getStt())
    			.statusDesc(PaymentEnum.getPaymentEnum(entity.getLastStatus()).getString())
    			.tanggal(tgl)
    			.userId(entity.getBookingCode().getUserId().getUserId())
    			.userProcess(admin==null?"SYSTEM":admin.getUserId())
    			.volumeWeight(entity.getLastVolume().toString())
    			.weight(entity.getLastGrossWeight().toString())
    			.status(entity.getLastStatus())
    			.userProcessName(adminName)
    			.build();
    }
}
