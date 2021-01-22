package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MPermohonanEntity;
import com.kahago.kahagoservice.entity.TPermohonanEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.List;
import java.util.Optional;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 8 Jun 2020
 */
@Repository
public interface MPermohonanRepo extends JpaRepository<MPermohonanEntity,String> {
	@Query(value="Select substr(nomor_permohonan,3,5) from m_permohonan order by cast(substr(nomor_permohonan,3,5) as decimal(5,0)) desc limit 1",
			nativeQuery=true)
	public String findLastNomor();
	
	Optional<MPermohonanEntity> findByNomorPermohonan(String nomorPermohonan);
	
	Optional<MPermohonanEntity> findByNomorPermohonanAndStatus(String nomorPermohonan,Integer status);
	@Query("SELECT M FROM MPermohonanEntity M WHERE M.nomorPermohonan=?1 and M.status <> ?2")
	Optional<MPermohonanEntity> findByNomorPermohonanAndStatusNotIn(String nomorPermohonan,Integer status);
}
