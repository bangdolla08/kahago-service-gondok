package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MOfficeEntity;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Hendro yuwono
 */
public interface MOfficeRepo extends PagingAndSortingRepository<MOfficeEntity, String> {
	List<MOfficeEntity> findAll();
	List<MOfficeEntity> findAllByParentOffice(String parentOffice);
	MOfficeEntity findAllByOfficeCode(String officeCode);
	MOfficeEntity findByOfficeCode(String officeCode);
	
}
