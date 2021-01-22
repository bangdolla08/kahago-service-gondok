package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MBankEntity;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Hendro yuwono
 */
public interface MBankRepo extends PagingAndSortingRepository<MBankEntity, String> {
	Optional<MBankEntity> findByBankCode(String bankCode);
}
