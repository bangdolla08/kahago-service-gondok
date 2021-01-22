package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MAreaKotaEntity;
import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TCategoryPickupTimeEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.entity.TPickupEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.PickupDto;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MPickupTimeRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TCategoryPickupTimeRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TPickupRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

/**
 * @author Hendro yuwono
 */
/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 23 Jul 2020
 */
@Service
public class PickupService {

    private static final Integer ACTIVE = 1;
    @Autowired
    private TPickupDetailRepo pickupDetailRepo;
    @Autowired
    private MPickupTimeRepo mastPickupTimeRepo;
    @Autowired
    private TPickupRepo pickupRepo;
    @Autowired
    private MUserRepo mUserRepo;
    @Autowired
    private TCategoryPickupTimeRepo tCategoryPickupTimeRepo;
    @Autowired
    private MUserCategoryRepo mUserCategoryRepo;
    @Autowired
    private BookService bookService;
    @Autowired
    private FreedayService freedayService;

    public List<ResPickupTime> findByTimeNow(String userId) {
        LocalTime atThisTime = LocalTime.now();
        LocalDate currentDay = LocalDate.now();
        List<ResPickupTime> response = new ArrayList<>();
        MUserEntity user = mUserRepo.getMUserEntitiesBy(userId);
        List<TCategoryPickupTimeEntity> lPickupTime = tCategoryPickupTimeRepo.findByIdUserCategoryAndActived(user.getUserCategory(), true);
        List<MPickupTimeEntity> entities = new ArrayList<MPickupTimeEntity>();
        if(user.getUserCategory().getAccountType().equals(ACTIVE)) {
        	entities = mastPickupTimeRepo.findByIsActive(ACTIVE);
        }else {
        	for(TCategoryPickupTimeEntity catPickupTime:lPickupTime) {
            	if(catPickupTime.getIdPickupTime().getIsActive().equals(ACTIVE)) {
            		entities.add(catPickupTime.getIdPickupTime());
            	}
            }
        }
        entities.sort(Comparator.comparing(MPickupTimeEntity::getTimeFrom));

        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        if(!freedayService.checkHoliday(currentDay)) {
        	if (dayOfWeek == DayOfWeek.SATURDAY.getValue()) {
        		LocalDate nextDay = bookService.getPickupDate(currentDay.plusDays(2));
                response.addAll(filterTimeGraterThanNow(entities, atThisTime, currentDay));
                response.addAll(filterTimeLessThanNow(entities, atThisTime, DateTimeUtil.getNameDay(nextDay)+", ", nextDay));
            } else if (dayOfWeek == DayOfWeek.SUNDAY.getValue()) {
            	LocalDate nextDay = bookService.getPickupDate(currentDay.plusDays(1));
                response.addAll(filterTimeLessThanNow(entities, atThisTime, DateTimeUtil.getNameDay(nextDay)+", ", nextDay));
            } 
            else {
            	LocalDate nextDay = bookService.getPickupDate(currentDay.plusDays(1));
                response.addAll(filterTimeGraterThanNow(entities, atThisTime, currentDay));
                entities = entities.stream().filter(p->p.getCurrentDay()==0).collect(Collectors.toList());
                if(!freedayService.checkHoliday(currentDay.plusDays(1))) {
                	response.addAll(filterTimeLessThanNow(entities, atThisTime, "Besok, ", currentDay.plusDays(1)));
                }
                else
                	response.addAll(filterTimeLessThanNow(entities, atThisTime, DateTimeUtil.getNameDay(nextDay)+", ", nextDay));
            }
        }else {
        	entities = entities.stream().filter(p->p.getCurrentDay()==0).collect(Collectors.toList());
        	LocalDate nextDay = bookService.getPickupDate(currentDay.plusDays(1));
        	response.addAll(filterTimeLessThanNow(entities, atThisTime, DateTimeUtil.getNameDay(nextDay)+", ", nextDay));
        }
        
        return response;
    }

    public List<ResPickupTime> findPickupTimeAsaign(){
        LocalDate currentDay = LocalDate.now();
        LocalTime atThisTime = LocalTime.now();
        List<MPickupTimeEntity> entities =mastPickupTimeRepo.findByIsActive(ACTIVE);
        return entities.stream().map(v->convertToPickupTimeModel(v,"",currentDay)).collect(Collectors.toList());
    }

    private ResPickupTime convertToPickupTimeModel(MPickupTimeEntity entity, String templateOfTime, LocalDate date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return ResPickupTime.builder()
                .pickupTimeId(entity.getIdPickupTime())
                .pickupDate(date.format(dateFormatter))
                .pickupTime(templateOfTime + entity.getTimeFrom().format(timeFormatter) + " - " + entity.getTimeTo().format(timeFormatter))
                .build();
    }

    private List<ResPickupTime> filterTimeGraterThanNow(List<MPickupTimeEntity> entity, LocalTime time, LocalDate date) {
        return entity.stream().filter(v -> v.getTimeFrom().isAfter(time)).map(v -> convertToPickupTimeModel(v, "Hari ini, ", date)).collect(Collectors.toList());
    }
    public TPickupDetailEntity getPickupDetail(String bookingCode) {
        return pickupDetailRepo.findFirstByBookIdBookingCode(bookingCode);
    }
    public TPickupDetailEntity getPickupDetail(String bookingCode,String courierId) {
        return pickupDetailRepo.findFirstByBookIdBookingCodeAndPickupIdCourierIdUserId(bookingCode,courierId);
    }
    public TPickupDetailEntity getPickupDetailByBookId(String pickupId){
        return pickupDetailRepo.findFirstByPickupOrderRequestEntityPickupOrderId(pickupId);
    }
    private List<ResPickupTime> filterTimeLessThanNow(List<MPickupTimeEntity> entity, LocalTime time, String templateOfTime, LocalDate date) {
        return entity.stream().map(v -> convertToPickupTimeModel(v, templateOfTime, date)).collect(Collectors.toList());
    }

    public List<TPickupDetailEntity> pickupPickupDetail(String officeCode){
        return pickupDetailRepo.findAllByPickupIdOfficeCodeCourierTask(officeCode);
    }
    
    public List<TPickupDetailEntity> pickupByOrderRequest(String officeCode){
    	return pickupDetailRepo.findAllByPickupIdOfficeCodeCourier(officeCode);
    }

    public List<TPickupDetailEntity> pickupByOrderRequest(List<MAreaKotaEntity> officeCode){
    	return pickupDetailRepo.findAllByPickupIdOfficeCodeCourier(officeCode);
    }
    public PickupDto createPickupDto(Integer pickupId){
    	List<Integer> status = new ArrayList<Integer>(Arrays.asList(
    		PickupDetailEnum.ASSIGN_PICKUP.getValue(),
    		PickupDetailEnum.IN_COURIER.getValue()
    	));
        return PickupDto.builder().pickupEntity(pickupRepo.getOne(pickupId))
                .countProcessed(pickupDetailRepo.countByPickupIdIdPickupAndStatus(pickupId, status))
                .countQty(pickupDetailRepo.countByPickupIdIdPickup(pickupId))
                .build();
    }
    public PickupDto createPickupDto(TPickupEntity pickupEntity){
        return createPickupDto(pickupEntity.getIdPickup());
    }

    public TPickupEntity savePickupEntity(TPickupEntity pickupEntity){
        return this.pickupRepo.save(pickupEntity);
    }

    public String createCodePickup(){
        String code = "0000";
        String head = "M";
        String lastcode = pickupRepo.findTopByIdPickupOrderByIdPickupCourierIdDesc().getCode();
        if(lastcode!=null) {
            code = Common.getCounter(lastcode,1,5);//getCounter(lastcode,1,5);
        }else {
            code = "0001";
        }
        return head + code;
    }
    public MPickupTimeEntity getPickupTimeEntity(Integer pickupTime){
        return this.mastPickupTimeRepo.findByIdPickupTime(pickupTime);
    }
    public TPickupDetailEntity savePickupDetailEntity(TPickupDetailEntity entity){
        return this.pickupDetailRepo.save(entity);
    }
    public List<TPickupDetailEntity> savePickupDetailEntity(List<TPickupDetailEntity> entity){
        return this.pickupDetailRepo.saveAll(entity);
    }
    public List<TPickupDetailEntity> getListPickupDraft(){
        return this.pickupDetailRepo.findByPickupIdStatus(PickupEnum.DRAFT.getValue());
    }

    public Map findByManifest(String manifestCode) {
        TPickupEntity entity = pickupRepo.findByCode(manifestCode);

        Map<String, Object> param = new HashMap<>();
        param.put("tgl", entity.getPickupDate().format(DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy")));
        param.put("waktu", entity.getTimePickupFrom().format(DateTimeFormatter.ofPattern("KK:mm")) + "-" + entity.getTimePickupTo().format(DateTimeFormatter.ofPattern("KK:mm")));
        param.put("assignby", entity.getCreateBy());
        param.put("code", entity.getCode());
        param.put("kurir",entity.getCourierId().getName());
        param.put("kurirtelp", entity.getCourierId().getHp());
        param.put("logo", "/home/kaha/reports/img/logo-kaha.jpeg");

        return param;
    }
    
    public TPickupDetailEntity findByPickupOrderReq(TPickupOrderRequestEntity entity) {
    	return pickupDetailRepo.findByPickupOrderRequestEntity(entity);
    }
    
    public List<ResPickupTime> findPickupTimeByUserCategory(Integer idUserCategory) {
    	MUserCategoryEntity ucat = mUserCategoryRepo.findById(idUserCategory).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
    	List<TCategoryPickupTimeEntity> lPickupTime = tCategoryPickupTimeRepo.findByIdUserCategory(ucat);
    	List<ResPickupTime> result = new ArrayList<ResPickupTime>();
    	for(TCategoryPickupTimeEntity catPickup:lPickupTime) {
    		if(catPickup.getIdPickupTime().getIsActive().equals(ACTIVE)) {
    		ResPickupTime rpt = ResPickupTime.builder()
    							.pickupTimeId(catPickup.getIdPickupTime().getIdPickupTime())
    							.pickupTime(catPickup.getIdPickupTime().getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+"-"+catPickup.getIdPickupTime().getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
    							.isActived(catPickup.getActived())
    							.idUserCategory(idUserCategory)
    							.build();
    		result.add(rpt);
    		}
    	}
    	return result;
    }
    @Transactional
    public SaveResponse activeOrUnActivedTimePickup(Integer idUserCategory,Integer pickupTimeId) {
    	MUserCategoryEntity ucat = mUserCategoryRepo.findById(idUserCategory).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
    	MPickupTimeEntity timePickup = mastPickupTimeRepo.findById(pickupTimeId).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
    	TCategoryPickupTimeEntity catTimePickup = tCategoryPickupTimeRepo.findByIdUserCategoryAndIdPickupTime(ucat, timePickup);
    	if(catTimePickup == null) throw new NotFoundException("Data Tidak Ditemukan");
    	if(catTimePickup.getActived()) {
    		catTimePickup.setActived(false);
    	}else {
    		catTimePickup.setActived(true);
    	}
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Aktif / NonAktif Time Pickup")
    			.build();
    }
    @Transactional
    public SaveResponse addTimePickup(Integer idUserCategory,Integer pickupTimeId) {
    	MUserCategoryEntity ucat = mUserCategoryRepo.findById(idUserCategory).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
    	MPickupTimeEntity timePickup = mastPickupTimeRepo.findById(pickupTimeId).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
    	TCategoryPickupTimeEntity catTimePickup = tCategoryPickupTimeRepo.findByIdUserCategoryAndIdPickupTime(ucat, timePickup);
    	if(catTimePickup!=null)throw new NotFoundException("Data Sudah Ada !");
    	catTimePickup = new TCategoryPickupTimeEntity();
    	catTimePickup.setIdUserCategory(ucat);
    	catTimePickup.setIdPickupTime(timePickup);
    	catTimePickup.setActived(true);
    	tCategoryPickupTimeRepo.save(catTimePickup);
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Simpan Time Pickup")
    			.build();    	
    }
    
    public void savePickupDetail(TPickupDetailEntity pickupDetail) {
    	TPickupDetailEntity pickup = null; 
    	if(pickupDetail.getBookId()==null) {
    		pickup = pickupDetail;
    	}else {
    		pickup = pickupDetailRepo.findByBookId(pickupDetail.getBookId()).stream().findAny().orElse(pickupDetail);
    	}
    	
    	pickupDetailRepo.save(pickup);
    }

    public List<TPickupDetailEntity> findByPickupIdAndStatus(TPickupEntity entity, Integer status){
        return pickupDetailRepo.findAllByPickupIdAndStatus(entity, status);
    }
    public List<TPickupDetailEntity> findByCourier(String courierUser,List<Integer> status){
        return pickupDetailRepo.findByCourier(courierUser,status);
    }
    
    public List<TPickupDetailEntity> findAllByCourier(String courierUser,List<Integer> status){
        return pickupDetailRepo.findAllByCourier(courierUser,status);
    }


}
