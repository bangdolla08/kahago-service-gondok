package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MFreedateEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MFreedateRepo extends JpaRepository<MFreedateEntity, Integer>{
	MFreedateEntity findByTahunAndBulanAndTgl(Integer tahun,Integer bulan,Integer tgl);
}
