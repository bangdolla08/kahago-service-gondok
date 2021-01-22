package com.kahago.kahagoservice.service;
/**
 * @author Ibnu Wasis
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.entity.MAreaKotaEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.model.response.OfficeCodeResponse;
import com.kahago.kahagoservice.repository.MOfficeRepo;

@Service
public class OfficeCodeService {

	@Autowired
	private MOfficeRepo mOfficeRepo;
	@Autowired
	private AreaService areaService;

	public List<MOfficeEntity> getBranchList(String officeCode){
		List<MOfficeEntity> entities = mOfficeRepo.findAll();
		List<MOfficeEntity> result = new ArrayList<>();
		result.add(entities.stream().filter(v -> v.getOfficeCode().equals(officeCode)).findFirst().orElseThrow(() -> new NotFoundException("Office code is not found")));
		findExistChild(entities, result, officeCode);
		return result;
	}

	public MOfficeEntity getBranch(String officeCode){
		return mOfficeRepo.findAllByOfficeCode(officeCode);
	}

	private void findExistChild(List<MOfficeEntity> parent, List<MOfficeEntity> result, String key) {
		if (parent.size() != 0) {
			for (MOfficeEntity entity : parent) {
				if (entity.getParentOffice().equals(key)) {
					result.add(entity);
					findExistChild(parent, result, entity.getOfficeCode());
				}
			}
		}
	}

	public Set<Integer> transformToCityIds(String officeCode) {
		boolean existsOffice = mOfficeRepo.existsById(officeCode);
		if (!existsOffice) {
			throw new NotFoundException();
		}

		List<MOfficeEntity> all = mOfficeRepo.findAll();
		Integer areaCityIdParent = all.stream().filter(v -> v.getOfficeCode().equals(officeCode))
				.map(MOfficeEntity::getAreaKotaId)
				.findAny()
				.orElseThrow(NotFoundException::new);

		Set<Integer> result = new HashSet<>();
		findExistChilds(all, result, officeCode);
		result.add(areaCityIdParent);

		return result;
	}

	private void findExistChilds(List<MOfficeEntity> parent, Set<Integer> result, String key) {
		if (parent.size() != 0) {
			for (MOfficeEntity entity : parent) {
				if (entity.getParentOffice().equals(key)) {
					result.add(entity.getAreaKotaId());
					findExistChilds(parent, result, entity.getOfficeCode());
				}
			}
		}
	}

	public List<String> officeCodeList(String parentUserid){
		return getBranchList(parentUserid).stream().map(this::toOfficeCodeString).collect(Collectors.toList());
	}
	public List<String> regionCodeList(String parentUserId){
		return getBranchList(parentUserId).stream().map(this::toRegionCode).collect(Collectors.toList());
	}

	private String toOfficeCodeString(MOfficeEntity mOfficeEntity){
		return mOfficeEntity.getOfficeCode();
	}
	private String toRegionCode(MOfficeEntity mOfficeEntity){
		return mOfficeEntity.getRegionCode();
	}

	public List<OfficeCodeResponse> getAll(){
		List<MOfficeEntity> lresponse = new ArrayList<MOfficeEntity>();
		for(MOfficeEntity of:mOfficeRepo.findAll()) {
			if(mOfficeRepo.findAll().size() > 0) {
				if(of.getIsActive().equals(Byte.parseByte("1"))) {
					lresponse.add(of);
				}
					
			}
		}
		return lresponse.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	private OfficeCodeResponse toDto(MOfficeEntity entity) {
		return OfficeCodeResponse.builder()
				.officeCode(entity.getOfficeCode())
				.parrentOffice(entity.getParentOffice())
				.name(entity.getName())
				.address(entity.getAddress())
				.city(entity.getCity())
				.unitType(entity.getUnitType())
				.postalCode(entity.getPostalCode())
				.telp(entity.getTelp())
				.fax(entity.getFax())
				.statusLayanan(entity.getStatusLayanan())
				.build();
	}

}
