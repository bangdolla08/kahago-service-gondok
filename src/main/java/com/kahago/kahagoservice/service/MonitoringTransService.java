package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.model.response.MonitorManifestResponse;
import com.kahago.kahagoservice.model.response.MonitorTransResponse;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@Service
public class MonitoringTransService {

    @Autowired
    private TPaymentRepo paymentRepo;
    @Autowired
	private OfficeCodeService officeService;
    @Autowired
    private TPickupDetailRepo tDetailRepo;

    public MonitorTransResponse countingByStatus(String key,String origin) {
    	List<String> office = null;		
        if (key.equals("day")) {
            return countByDay(origin);
        } else {
            return countByMonth(origin);
        }
    }

    private MonitorTransResponse countByDay(String origin) {
        ArrayList<Long> totalOfDay = new ArrayList<>();
        ArrayList<Long> totalOfDaySby = new ArrayList<>();
        ArrayList<Long> totalOfDaySda = new ArrayList<>();
        ArrayList<String> reOrigin = new ArrayList<>(Arrays.asList("Total","SUB","SDA"));
        
        for (int x = 0; x < 8; x++) {
            Long projs = paymentRepo.countStatusByDay(LocalDate.now().minusDays(x),origin);
            totalOfDay.add(projs == null ? 0 : projs);
        }
        for (int x = 0; x < 8; x++) {
            Long projs = paymentRepo.countStatusByDay(LocalDate.now().minusDays(x),"Surabaya");
            totalOfDaySby.add(projs == null ? 0 : projs);
        }
        for (int x = 0; x < 8; x++) {
            Long projs = paymentRepo.countStatusByDay(LocalDate.now().minusDays(x),"Sidoarjo");
            totalOfDaySda.add(projs == null ? 0 : projs);
        }
        ArrayList<ArrayList<Long>> result = new ArrayList<>();
        result.add(totalOfDay);
        result.add(totalOfDaySby);
        result.add(totalOfDaySda);
        List<Long> resTotal = new ArrayList<>();
        return new MonitorTransResponse("day", reOrigin,result,resTotal);
    }

    private MonitorTransResponse countByMonth(String origin) {
        ArrayList<Long> totalOfMonth = new ArrayList<>();
        ArrayList<Long> totalOfMonthSby = new ArrayList<>();
        ArrayList<Long> totalOfMonthSda = new ArrayList<>();
        ArrayList<String> reOrigin = new ArrayList<>(Arrays.asList("Total","SUB","SDA"));
        for (int x = 0; x < 4; x++) {
            LocalDate date = LocalDate.now().minusMonths(x);
            Long projs = paymentRepo.countStatusByMonth(date.getMonth().getValue(), date.getYear(), origin);
            totalOfMonth.add(projs == null ? 0 : projs);
        }
        for (int x = 0; x < 4; x++) {
            LocalDate date = LocalDate.now().minusMonths(x);
            Long projs = paymentRepo.countStatusByMonth(date.getMonth().getValue(), date.getYear(), "Surabaya");
            totalOfMonthSby.add(projs == null ? 0 : projs);
        }
        for (int x = 0; x < 4; x++) {
            LocalDate date = LocalDate.now().minusMonths(x);
            Long projs = paymentRepo.countStatusByMonth(date.getMonth().getValue(), date.getYear(), "Sidoarjo");
            totalOfMonthSda.add(projs == null ? 0 : projs);
        }
        ArrayList<ArrayList<Long>> result = new ArrayList<>();
        result.add(totalOfMonth);
        result.add(totalOfMonthSby);
        result.add(totalOfMonthSda);
        List<Long> resTotal = new ArrayList<>();
        return new MonitorTransResponse("month",reOrigin, result,resTotal);
    }
    
    public List<MonitorManifestResponse> getTotalPendingManifestByPickupDate(){
    	List<TPickupDetailEntity> lPickupDtl = tDetailRepo.getPendingManifestByPickupDate();
    	List<TPickupDetailEntity> lPickupReq = tDetailRepo.getPendingManifestReq();
    	List<MonitorManifestResponse> result = new ArrayList<>();
    	
    	for(TPickupDetailEntity dtl : lPickupDtl) {
    		Integer total = tDetailRepo.getTotalPendingManifestByPickupDateAndPickupTimeId(dtl.getPickupId().getPickupDate(), dtl.getPickupId().getTimePickupId().getIdPickupTime());
    		String pickupDate = dtl.getPickupId().getPickupDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    		if(dtl.getPickupId().getPickupDate().equals(LocalDate.now())) {
    			pickupDate = "Hari ini";
    		}else if(dtl.getPickupId().getPickupDate().equals(LocalDate.now().minusDays(1L))) {
    			pickupDate = "Kemarin";
    		}
    		MonitorManifestResponse response = MonitorManifestResponse.builder()
						.totalPickup(total)
						.pickupTime(pickupDate+", "+dtl.getPickupId().getTimePickupFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+
						dtl.getPickupId().getTimePickupTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
						.pickupDate(dtl.getPickupId().getPickupDate().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
						.pickupTimeId(dtl.getPickupId().getTimePickupId().getIdPickupTime())
						.build();
    			result.add(response);
    	}
    	for(TPickupDetailEntity dtl:lPickupReq) {
    		Boolean flag = false;
    		Integer total = tDetailRepo.getTotalPendingManifestReqByPickupDateAndPickupTimeId(dtl.getPickupId().getPickupDate(), dtl.getPickupId().getTimePickupId().getIdPickupTime());
    		String pickupDate = dtl.getPickupId().getPickupDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    		if(dtl.getPickupId().getPickupDate().equals(LocalDate.now())) {
    			pickupDate = "Hari ini";
    		}else if(dtl.getPickupId().getPickupDate().equals(LocalDate.now().minusDays(1L))) {
    			pickupDate = "Kemarin";
    		}
    		for(MonitorManifestResponse resp : result) {
    			String pikcupTime = pickupDate+", "+dtl.getPickupId().getTimePickupFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+
						dtl.getPickupId().getTimePickupTo().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    			if(resp.getPickupTime().equalsIgnoreCase(pikcupTime)) {
    				resp.setTotalPickup(resp.getTotalPickup()+total);
    				flag = true;
    			}
    		}
    		if(!flag) {
    			MonitorManifestResponse response = MonitorManifestResponse.builder()
						.totalPickup(total)
						.pickupTime(pickupDate+", "+dtl.getPickupId().getTimePickupFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+
						dtl.getPickupId().getTimePickupTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
						.pickupDate(dtl.getPickupId().getPickupDate().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
						.pickupTimeId(dtl.getPickupId().getTimePickupId().getIdPickupTime())
						.build();
    			result.add(response);
    		}
    	}
    	Comparator<MonitorManifestResponse> sortPickupTime = Comparator.comparing(MonitorManifestResponse::getPickupDate).thenComparing(MonitorManifestResponse::getPickupTimeId);
    	Collections.sort(result, sortPickupTime);
    	return result;
    }
}
