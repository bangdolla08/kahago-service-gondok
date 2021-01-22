package com.kahago.kahagoservice.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.*;
import com.kahago.kahagoservice.model.projection.PickupAddress;
import com.kahago.kahagoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.ManifestMoveReq;
import com.kahago.kahagoservice.service.HistoryTransactionService;
import com.kahago.kahagoservice.service.PickupService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ManifestManagementService {
	
	@Autowired
	private TPickupRepo pickupRepo;
	@Autowired
	private TPickupDetailRepo pickupDetailRepo;
	@Autowired
	private MUserRepo userRepo;
	@Autowired
    private PickupService pickupService;
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private MAreaRepo areaRepo;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	@Autowired
	private TPaymentHistoryRepo tPaymentHistoryRepo;

	@Autowired
	private TPickupAddressRepo tPickupAddressRepo;
	@Autowired
	private TCourierPickupRepo courierPickupRepo;
	
	public List<ManifestListResp> getListManifest(String originCity,String noManifest){
		MAreaEntity mAreaEntity=areaRepo.findByKotaEntityAreaKotaId(Integer.parseInt(originCity));
		List<TPickupDetailEntity> lsPickupDetail = pickupDetailRepo.findByPickupIdStatusInAndBookIdOriginIgnoreCase(getStatusFilter(), mAreaEntity.getAreaName(),noManifest);
		Map<TPickupEntity, Long> mpPickup = lsPickupDetail.stream().collect(Collectors.groupingBy(TPickupDetailEntity::getPickupId,Collectors.counting()));
		List<TPickupEntity> lsPickup = new ArrayList<>(mpPickup.keySet());
		List<ManifestResp> lsManifestResp = lsPickup.stream().map(this::toMani).collect(Collectors.toList());
		Map<String, List<ManifestResp>> mpManifest = lsManifestResp.stream()
				.collect(Collectors.groupingBy(ManifestResp::getCourierId, Collectors.toList()));
			
		List<ManifestListResp> lsManifest = new ArrayList<>();
		mpManifest.forEach((k,v)-> {
			ManifestListResp mp = new ManifestListResp();
			mp.setCourierId(k);
			mp.setManifests(v.stream().map(this::toManiList).collect(Collectors.toList()));
			lsManifest.add(mp);
		});
		return lsManifest;
	}
	
	private ManifestList toManiList(ManifestResp maniResp) {
		return new ManifestList(maniResp.getIdManifest(), maniResp.getNoManifest());
	}
	private ManifestResp toMani(TPickupEntity pickupEntity) {
		
		return ManifestResp.builder()
				.idManifest(pickupEntity.getIdPickup().toString())
				.noManifest(pickupEntity.getCode())
				.courierId(pickupEntity.getCourierId().getUserId())
				.build();
	}
	
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public SaveResponse doUpdateManifest(ManifestMoveReq req,String userLogin) {

		Integer idPickupDest;
		Boolean isDest = !req.getCodeManifestDestination().isEmpty();
		TPickupEntity pickupOrigin = pickupRepo.findByCode(req.getCodeManifestOrigin());
		MUserEntity user = userRepo.findById(req.getCourierIdDestination()).orElseThrow(()->new NotFoundException("User Kurir tidak ditemukan"));
		List<Integer> lsStatus = new ArrayList<>();
		lsStatus.add(PaymentEnum.DRAFT_PICKUP.getCode());
		lsStatus.add(PaymentEnum.ASSIGN_PICKUP.getValue());
		List<TPaymentEntity> lsPayment = payRepo.findByBookingCodeInAndStatusIn(req.getBooks(), lsStatus);
		List<TPickupDetailEntity> lsPickupDetailold = pickupDetailRepo.findByBookIdIn(lsPayment);
		if(isDest) {
			List<TPickupDetailEntity> lsPickupDetailDest = pickupDetailRepo.findByNoManifest(req.getCodeManifestDestination(), req.getCourierIdDestination());
			TPickupDetailEntity pickupDetailEntity = lsPickupDetailDest.stream().findAny().get();
			String originDest = pickupDetailEntity.getBookId().getOrigin();
			TPickupEntity pickupDest = pickupDetailEntity.getPickupId();
			//pickupRepo.findByCode(req.getCodeManifestDestination());
			
			lsPickupDetailold =  lsPickupDetailold.stream()
				.filter(p->p.getBookId().getOrigin().equalsIgnoreCase(originDest))
				.collect(Collectors.toList());
			if(lsPickupDetailold.size()==0) {
				throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, ResponseStatus.NOT_CHANGED.getReasonPhrase());
			}
			lsPickupDetailold.forEach(p->p.setPickupId(pickupDest));
			pickupDetailRepo.saveAll(lsPickupDetailold);
			idPickupDest = pickupDest.getIdPickup();
		}else {
			TPickupEntity pickupDest = TPickupEntity.builder()
					.courierId(user)
					.code(pickupService.createCodePickup())
					.createAt(LocalDateTime.now())
					.createBy(userLogin)
					.status(PickupEnum.ASSIGN_PICKUP.getValue())
					.pickupDate(LocalDate.now())
					.timePickupId(pickupOrigin.getTimePickupId())
					.timePickupFrom(pickupOrigin.getTimePickupFrom())
					.timePickupTo(pickupOrigin.getTimePickupTo())
					.build();
			
			TPickupEntity pickupDest2 =  pickupService.savePickupEntity(pickupDest);
			List<TPickupDetailEntity> lsPickupDetail = lsPickupDetailold.stream().map(p-> toPickupDetail(p, pickupDest2,userLogin)).collect(Collectors.toList());
			pickupDetailRepo.saveAll(lsPickupDetail);

			idPickupDest = pickupDest2.getIdPickup();
		}
		
		int ttl = pickupDetailRepo.findByPickupId(pickupOrigin).size();
		if(ttl==0) {
			pickupOrigin.setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());
			pickupRepo.save(pickupOrigin);
		}
		//create payment history
		for(TPaymentEntity pay : lsPayment) {
			historyTransactionService.createHistory(pay, pay, userLogin);
			TPaymentHistoryEntity pHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(pay, pay.getStatus());
			pHistory.setReason("Courier Name : "+user.getName());
			tPaymentHistoryRepo.save(pHistory);
		}

		reRoutingPickupCourier(pickupOrigin.getIdPickup(), idPickupDest);
		return SaveResponse.builder()
        		.saveStatus(1)
        		.saveInformation("Berhasil")
        		.linkResi("")
        		.build();
	}

	private void reRoutingPickupCourier(Integer idPickupOrigin, Integer idPickupDest) {
		List<PickupAddress> pickCourierOrigin = pickupDetailRepo.courierStatusInAssignPickup(idPickupOrigin);
		excutePickupCourier(pickCourierOrigin);


		List<PickupAddress> pickCourierDest = pickupDetailRepo.courierStatusInAssignPickup(idPickupDest);
		excutePickupCourier(pickCourierDest);
	}

	private void excutePickupCourier(List<PickupAddress> listOfPickupAddress) {
		intersectOfCourierPickupAndRemove(listOfPickupAddress);
		intersectOfPickupAddressAndInsert(listOfPickupAddress);
	}

	private void intersectOfPickupAddressAndInsert(List<PickupAddress> listOfPickupAddress) {
		Set<Integer> courierPick = courierPickupRepo.findByPickupIdPickup(listOfPickupAddress.get(0).getPickupId())
				.stream()
				.map(TCourierPickupEntity::getPickupAddress)
				.map(TPickupAddressEntity::getPickupAddrId)
				.collect(Collectors.toSet());

		List<PickupAddress> pickupAddressList = listOfPickupAddress.stream().filter(v -> !courierPick.contains(v.getPickupAddressId())).collect(Collectors.toList());
		pickupAddressList.forEach(this::savingCourierPickup);
	}

	private void intersectOfCourierPickupAndRemove(List<PickupAddress> listOfPickupAddress) {
		Set<Integer> courierPick = listOfPickupAddress.stream().map(PickupAddress::getPickupAddressId).collect(Collectors.toSet());
		List<TCourierPickupEntity> courierPickups = courierPickupRepo.findByPickupIdPickup(listOfPickupAddress.get(0).getPickupId())
				.stream()
				.filter(v -> !courierPick.contains(v.getPickupAddress().getPickupAddrId())).collect(Collectors.toList());

		courierPickups.forEach(v -> courierPickupRepo.delete(v));
	}

	private void savingCourierPickup(PickupAddress pickupAddress) {
		TPickupEntity tPickup = pickupRepo.findById(pickupAddress.getPickupId()).orElseThrow(() -> new NotFoundException("pickup not found"));
		TPickupAddressEntity pickupAddressEntity = tPickupAddressRepo.findById(pickupAddress.getPickupAddressId()).orElseThrow(() -> new NotFoundException("pickup address not found"));
		TCourierPickupEntity entity = TCourierPickupEntity.builder()
				.courierId(pickupAddress.getCourierId())
				.pickup(tPickup)
				.pickupAddress(pickupAddressEntity)
				.status(statusCourierPickup(pickupAddress.getStatusPickup()))
				.build();

		courierPickupRepo.save(entity);
	}

	private Integer statusCourierPickup(Integer pickupStatus) {
		if (pickupStatus.equals(PickupEnum.DRAFT.getValue())) {
			return PickupCourierEnum.DRAFT.getValue();
		} else if (pickupStatus.equals(PickupEnum.ASSIGN_PICKUP.getValue())) {
			return PickupCourierEnum.READY_PICKUP.getValue();
		} else {
			return PickupCourierEnum.READY_PICKUP.getValue();
		}
	}

	private TPickupDetailEntity toPickupDetail(TPickupDetailEntity pickupDetail,TPickupEntity pickup,String userLogin) {
		TPickupDetailEntity pick = pickupDetail;
		pick.setPickupId(pickup);
		return pick;
	}
	
	private List<Integer> getStatusFilter(){
		List<Integer> lsStatus = new ArrayList<>();
		lsStatus.add(PickupEnum.DRAFT.getValue());
		lsStatus.add(PickupEnum.ASSIGN_PICKUP.getValue());
		return lsStatus;
	}
	
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ManifestResp{
	private String idManifest;
	private String courierId;
	private String noManifest;
}
