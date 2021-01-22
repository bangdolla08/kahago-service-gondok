package com.kahago.kahagoservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.entity.MGroupListEntity;
import com.kahago.kahagoservice.entity.MMenuEntity;
import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.MUserPriorityEntity;
import com.kahago.kahagoservice.entity.TCategoryPickupTimeEntity;
import com.kahago.kahagoservice.entity.TCategorySwitcherEntity;
import com.kahago.kahagoservice.entity.TOptionPaymentEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.MenuDetailReq;
import com.kahago.kahagoservice.model.request.OptionPayment;
import com.kahago.kahagoservice.model.request.UserCategoryRequest;
import com.kahago.kahagoservice.model.response.MenuDetails;
import com.kahago.kahagoservice.model.response.MenuList;
import com.kahago.kahagoservice.model.response.OptionPaymentListResponse;
import com.kahago.kahagoservice.model.response.OptionPaymentResponse;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UserCategoryResponse;
import com.kahago.kahagoservice.model.response.UserPriorityResponse;
import com.kahago.kahagoservice.model.response.VendorResponse;
import com.kahago.kahagoservice.repository.MGroupListRepo;
import com.kahago.kahagoservice.repository.MMenuRepo;
import com.kahago.kahagoservice.repository.MOptionPaymentRepo;
import com.kahago.kahagoservice.repository.MPickupTimeRepo;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserPriorityRepo;
import com.kahago.kahagoservice.repository.TCategoryPickupTimeRepo;
import com.kahago.kahagoservice.repository.TCategorySwitcherRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;

/**
 * @author Ibnu Wasis
 */
@Service
public class MasterUserCategoryService {
	@Autowired
	private MUserCategoryRepo mUserCategoryRepo;
	@Autowired
	private MUserPriorityRepo mUserPriorityRepo;
	@Autowired
	private TCategorySwitcherRepo tCategorySwitcherRepo;
	@Autowired
	private TCategoryPickupTimeRepo tCategoryPickupTimeRepo;
	@Autowired
	private MGroupListRepo mGroupListRepo;
	@Autowired
	private TOptionPaymentRepo tOptionPaymentRepo;
	@Autowired
	private MPickupTimeRepo mPickupTimeRepo;
	@Autowired
	private MSwitcherRepo mSwitcherRepo;
	@Autowired
	private MOptionPaymentRepo mOptionPaymentRepo;
	@Autowired
	private MMenuRepo menuRepo;
	
	public List<UserCategoryResponse> getAllUserCategory(){
		List<MUserCategoryEntity> lUserCategory = mUserCategoryRepo.findAll();
		
		return lUserCategory.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	private UserCategoryResponse toDto(MUserCategoryEntity userCategory) {
		MUserPriorityEntity entity = mUserPriorityRepo.findByUserCategory(userCategory.getSeqid());
		UserPriorityResponse userPrior = null;
		if(entity != null) {
			userPrior = UserPriorityResponse.builder()
					.request1(entity.getRequest1())
					.request2(entity.getRequest2())
					.request3(entity.getRequest3())
					.minKiriman(entity.getMinKiriman())
					.paylater(entity.getPaylater())
					.isResiAuto(entity.getIsResiAuto())
					.build();
		}
		return UserCategoryResponse.builder()
				.id(userCategory.getSeqid())
				.nameCategory(userCategory.getNameCategory())
				.accountType(userCategory.getAccountType().toString())
				.roleName(userCategory.getRoleName())
				.userPriority(userPrior)
				.build();
	}
	
	@Transactional
	public SaveResponse addOrEditCategoryUser(UserCategoryRequest request,String userAdmin) {
		MUserCategoryEntity userCat = new MUserCategoryEntity();
		MUserPriorityEntity userPriority = new MUserPriorityEntity();
		if(request.getIdUserCategory() != null) {
			userCat = mUserCategoryRepo.findBySeqid(request.getIdUserCategory());
			userPriority = mUserPriorityRepo.findByUserCategory(request.getIdUserCategory());
		}
		if(userPriority == null) {
			userPriority = new MUserPriorityEntity();
		}
		userCat.setAccountType(Integer.parseInt(request.getAccountType()));
		userCat.setNameCategory(request.getNameCategory());
		userCat.setRoleName("ROLE_"+request.getRoleName().toUpperCase());
		userCat.setLastUpdate(LocalDateTime.now());
		userCat.setLastUser(userAdmin);
		mUserCategoryRepo.save(userCat);
		userPriority.setUserCategory(userCat.getSeqid());
		userPriority.setMinKiriman(request.getMinKoli()==null?1:request.getMinKoli());
		userPriority.setPaylater(request.getPayLater()==null?false:request.getPayLater());
		userPriority.setRequest1(request.getRequestOne()==null?false:request.getRequestOne());
		userPriority.setRequest2(request.getRequestTwo()==null?false:request.getRequestTwo());
		userPriority.setRequest3(request.getRequestThree()==null?false:request.getRequestThree());
		userPriority.setIsResiAuto(request.getAutoResi()==null?false:request.getAutoResi());
		mUserPriorityRepo.save(userPriority);
		
		if(request.getIdUserCategory() == null) {
			List<MMenuEntity> lMenu = menuRepo.findAll();
			List<MGroupListEntity> lGroup = new ArrayList<>();
			for(MMenuEntity menu : lMenu) {
				MGroupListEntity entity = new MGroupListEntity();
				entity.setUserCategory(userCat.getSeqid());
				entity.setMenuId(menu);
				entity.setIsDelete(false);
				entity.setIsRead(false);
				entity.setIsWrite(false);
				lGroup.add(entity);
			}
			mGroupListRepo.saveAll(lGroup);
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil simpan Category Baru")
				.build();
	}
	//list pickup Time By User Category
	public List<ResPickupTime> getListPickupTimeByCategory(Integer userCategoryId){
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(userCategoryId);
		List<TCategoryPickupTimeEntity> listPickup = tCategoryPickupTimeRepo.findByIdUserCategoryAndActived(userCat, true);
		List<ResPickupTime> response = new ArrayList<ResPickupTime>();
		for(TCategoryPickupTimeEntity entity : listPickup) {
			ResPickupTime resp = ResPickupTime.builder()
								 .idUserCategory(userCat.getSeqid())
								 .isActived(entity.getActived())
								 .pickupTime(entity.getIdPickupTime().getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+"-"+
										 entity.getIdPickupTime().getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
								 .pickupTimeId(entity.getIdPickupTime().getIdPickupTime())
								 .build();
			response.add(resp);
		}
		
		return response;
	}
	
	@Transactional
	public SaveResponse addOrRemovePickupTimeUserCategory(UserCategoryRequest request) {
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(request.getIdUserCategory());
		//add time pickup or actived
		for(Integer idPickup : request.getPickupTimeId()) {
			MPickupTimeEntity pickupTime = mPickupTimeRepo.findByIdPickupTime(idPickup);
			TCategoryPickupTimeEntity pickupTimeCate = tCategoryPickupTimeRepo.findByIdUserCategoryAndIdPickupTime(userCat, pickupTime);
			if(pickupTimeCate == null) {
				pickupTimeCate = new TCategoryPickupTimeEntity();
				pickupTimeCate.setIdUserCategory(userCat);
				pickupTimeCate.setIdPickupTime(pickupTime);
				pickupTimeCate.setActived(true);
			}else {
				pickupTimeCate.setActived(true);
			}
			tCategoryPickupTimeRepo.save(pickupTimeCate);
		}
		//remove pickup time or disable
		List<TCategoryPickupTimeEntity> lPickupTime = tCategoryPickupTimeRepo.findByIdUserCategory(userCat);
		for(TCategoryPickupTimeEntity timeCategory : lPickupTime) {
			Boolean flagRemove = true;
			for(Integer pickupTime : request.getPickupTimeId()) {
				if(timeCategory.getIdPickupTime().getIdPickupTime().equals(pickupTime)) {
					flagRemove = false;
				}
			}
			if(flagRemove) {
				timeCategory.setActived(false);
				tCategoryPickupTimeRepo.save(timeCategory);
			}
		}
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Time Pickup ")
				.build();
	}
	//list vendor By User Category
	public List<VendorResponse> getAllVendorByUserCategory(Integer userCategoryId){
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(userCategoryId);
		List<VendorResponse> response = new ArrayList<>();
		List<TCategorySwitcherEntity> lSwitcher = tCategorySwitcherRepo.findAllByIdUserCategory(userCat.getSeqid());
		for(TCategorySwitcherEntity entity : lSwitcher) {
			VendorResponse resp = VendorResponse.builder()
									.swicherCode(entity.getSwitcherCode().getSwitcherCode())
									.displayName(entity.getSwitcherCode().getDisplayName())
									.name(entity.getSwitcherCode().getName())
									.build();
			response.add(resp);
		}
		return response;
	}
	
	//list Option Payment By User Category
	public List<OptionPaymentResponse> getAllOptionPaymentByUserCategory(Integer idUserCategory){
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(idUserCategory);
		List<OptionPaymentResponse> response = new ArrayList<>();
		List<TOptionPaymentEntity> lOptionPayment = tOptionPaymentRepo.findByUserCategory(userCat);
		for(TOptionPaymentEntity entity : lOptionPayment) {
			OptionPaymentResponse resp = OptionPaymentResponse.builder()
										.codePayment(entity.getOptionPayment().getCode())
										.description(entity.getOptionPayment().getDescription())
										.isDeposit(entity.getIsDeposit())
										.isPayment(entity.getIsPayment())
										.seqid(entity.getOptionPayment().getSeqid())
										.build();
			response.add(resp);
		}
		return response;
	}
	//list Menu By User Category
	public List<MenuList> getAllMenuByUserCategory(Integer idUserCategory){
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(idUserCategory);
		List<MenuList> response = new ArrayList<>();
		List<MGroupListEntity> lGroup = mGroupListRepo.findAllByUserCategory(userCat.getSeqid());
		for(MGroupListEntity entity : lGroup) {
			MenuList resp = MenuList.builder()
							.menuId(entity.getMenuId().getMenuId())
							.menuName(entity.getMenuId().getMenuName())
							.isDelete(entity.getIsDelete()==null?false:entity.getIsDelete())
							.isRead(entity.getIsRead()==null?false:entity.getIsRead())
							.isWrite(entity.getIsWrite()==null?false:entity.getIsWrite())
							.build();
			response.add(resp);
		}
		
		return response;
	}
	//Add Or Remove Vendor
	@Transactional
	public SaveResponse saveVendorUserCategory(UserCategoryRequest request) {
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(request.getIdUserCategory());
		List<TCategorySwitcherEntity> lswitcher = tCategorySwitcherRepo.findAllByIdUserCategory(userCat.getSeqid());
		//add Vendor
		for(Integer vendorId : request.getVendorCode()) {
			MSwitcherEntity vendor  = mSwitcherRepo.findById(vendorId).orElseThrow(()->new NotFoundException("Vendor Tidak Ditemukan !"));
			TCategorySwitcherEntity entity = tCategorySwitcherRepo.findByIdUserCategoryAndSwitcherCode(userCat.getSeqid(), vendor);
			if(entity == null) {
				entity = new TCategorySwitcherEntity();
				entity.setIdUserCategory(userCat.getSeqid());
				entity.setSwitcherCode(vendor);
				tCategorySwitcherRepo.save(entity);
			}
		}
		
		//remove vendor from tCategorySwitcher
		for(TCategorySwitcherEntity entity:lswitcher) {
			Boolean flagRemove = true;
			for(Integer vendorId:request.getVendorCode()) {
				if(entity.getSwitcherCode().getSwitcherCode().equals(vendorId)) {
					flagRemove = false;
				}
			}
			if(flagRemove) {
				tCategorySwitcherRepo.delete(entity);
			}
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Vendor ")
				.build();
		
	}
	//add or remove Option payment in t_option_payment
	@Transactional
	public SaveResponse saveOptionPaymentUserCategory(UserCategoryRequest request) {
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(request.getIdUserCategory());
		List<TOptionPaymentEntity> lOptionPayment = tOptionPaymentRepo.findByUserCategory(userCat);
		//add t_option_payment
		for(OptionPayment optionPay : request.getOptionPayment()) {
			MOptionPaymentEntity optPay = mOptionPaymentRepo.findBySeqid(optionPay.getOptionPaymentId());
			TOptionPaymentEntity entity = tOptionPaymentRepo.findByUserCategoryAndOptionPayment(userCat, optPay);
			if(entity == null) {
				entity = new TOptionPaymentEntity();
				entity.setUserCategory(userCat);
				entity.setOptionPayment(optPay);
				entity.setIsDeposit(optionPay.getIsDeposit());
				entity.setIsPayment(optionPay.getIsPayment());
				entity.setCode(optPay.getCode());
				tOptionPaymentRepo.save(entity);
			}
		}
		
		//remove t_option_payment
		for(TOptionPaymentEntity payOption:lOptionPayment) {
			Boolean flagRemove = true;
			for(OptionPayment optionPay : request.getOptionPayment()) {
				if(payOption.getOptionPayment().getSeqid().equals(optionPay.getOptionPaymentId())) {
					flagRemove = false;
				}
			}
			if(flagRemove) {
				tOptionPaymentRepo.delete(payOption);
			}
		}
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Option Payment ")
				.build();
	}
	//add Or Remove menu in m_group_list
	@Transactional
	public SaveResponse saveMenuByUserCategory(UserCategoryRequest request) {
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(request.getIdUserCategory());
		List<MGroupListEntity> lGropMenu = mGroupListRepo.findAllByUserCategory(userCat.getSeqid());
		//add menu in group list
//		for(MenuDetailReq menu:request.getMenu()) {
//			MMenuEntity menuEntity = menuRepo.findByMenuId(menu.getMenuId());
//			MGroupListEntity entity = mGroupListRepo.findByMenuIdMenuIdAndUserCategory(menuEntity.getMenuId(), userCat.getSeqid());
//			if(entity == null) {
//				entity = new MGroupListEntity();
//				entity.setMenuId(menuEntity);
//				entity.setIsDelete(menu.getIsDelete());
//				entity.setIsRead(menu.getIsRead());
//				entity.setIsWrite(menu.getIsWrite());
//				entity.setUserCategory(userCat.getSeqid());
//				mGroupListRepo.save(entity);
//			}
//		}
//		//remove menu group list
//		for(MGroupListEntity group : lGropMenu) {
//			Boolean flagRemove = true;
//			for(MenuDetailReq menu : request.getMenu()) {
//				if(group.getMenuId().getMenuId().equals(menu.getMenuId())) {
//					flagRemove = false;
//				}
//			}
//			if(flagRemove) {
//				mGroupListRepo.delete(group);
//			}
//		}
		//add menu in group list
		if(request.getMenuDetails() != null) {
			request.getMenuDetails().forEach(menu->{
				menu.getSubmenu().forEach(subMenu->{
					subMenu.getListChild().forEach(menuDetail->{
						MMenuEntity menuEntity = menuRepo.findById(menuDetail.getMenuId()).orElseThrow(()->new NotFoundException("Menu Tidak Ditemukan !"));
						MGroupListEntity entity = mGroupListRepo.findByMenuIdMenuIdAndUserCategory(menuEntity.getMenuId(), userCat.getSeqid());
						if(entity == null ) {
							entity = new MGroupListEntity();
							entity.setUserCategory(userCat.getSeqid());
							entity.setMenuId(menuEntity);
							entity.setIsDelete(menuDetail.getIsDelete()==null?false:menuDetail.getIsDelete());
							entity.setIsRead(menuDetail.getIsRead()==null?false:menuDetail.getIsRead());
							entity.setIsWrite(menuDetail.getIsWrite()==null?false:menuDetail.getIsWrite());
							mGroupListRepo.save(entity);
						}else {
							entity.setUserCategory(userCat.getSeqid());
							entity.setMenuId(menuEntity);
							entity.setIsDelete(menuDetail.getIsDelete()==null?false:menuDetail.getIsDelete());
							entity.setIsRead(menuDetail.getIsRead()==null?false:menuDetail.getIsRead());
							entity.setIsWrite(menuDetail.getIsWrite()==null?false:menuDetail.getIsWrite());
							mGroupListRepo.save(entity);
						}
					});
				});
			});
		}
		
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Menu ")
				.build();
	}
	public UserCategoryResponse getUserCategoryById(Integer id) {
		MUserCategoryEntity userCat = mUserCategoryRepo.findBySeqid(id);
		return toDto(userCat);
	}
}
