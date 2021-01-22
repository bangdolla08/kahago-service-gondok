package com.kahago.kahagoservice.repository;
/**
 * @author Ibnu Wasis
 */

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kahago.kahagoservice.entity.TLeadTimeEntity;

public interface TLeadTimeRepo extends JpaRepository<TLeadTimeEntity, Integer>{
	TLeadTimeEntity findByBookingCode(String bookingCode);
	@Query("select LT from TLeadTimeEntity LT where LT.trxDate between ?1 and ?2")
	List<TLeadTimeEntity> findByTrxDate(LocalDate startDate,LocalDate endDate);
	
	@Query(value="select count(l.status),l.status,pw.product_sw_code from t_lead_time l " + 
			"join t_payment p on l.booking_code = p.booking_code " + 
			"join m_product_switcher pw on p.product_sw_code = pw.product_sw_code " + 
			"where (?1 is null or pw.product_sw_code = ?1) and (?2 is null or p.origin = ?2) and (?4 is null or pw.switcher_code = ?4) "
			+ "and (?3 is null or p.user_id = ?3) and p.trx_date between ?5 and ?6 " + 
			"group by l.status,pw.product_sw_code",nativeQuery=true)
	List<Object[]> getTotalLeadTimeByProduct(Integer productSwCode,String areaId,String userId,Integer switcherCode,LocalDate startDate,LocalDate endDate);
	
	@Query(value="select tp.trx_date," + 
			"mo.name as office,"+
			"tp.user_id," + 
			"tp.booking_code," + 
			"ms.name as vendor," + 
			"mp.name as product," + 
			"tp.stt," + 
			"tp.origin," + 
			"tp.destination," + 
			"lt.time_leave," + 
			"lt.time_arrived," + 
			"lt.status," + 
			"mp.product_sw_code," + 
			"tp.id_postal_code " + 
			"from t_lead_time lt " + 
			"join t_payment tp on lt.booking_code = tp.booking_code " + 
			"join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " + 
			"join m_office mo on tp.office_code = mo.office_code " + 
			"join m_switcher ms on mp.switcher_code = ms.switcher_code " + 
			"where tp.trx_date between ?5 and ?6 and (?4 is null or ms.switcher_code = ?4) and (?3 is null or tp.user_id = ?3) "
			+ "and (?1 is null or mp.product_sw_code = ?1) and (?2 is null or tp.origin = ?2) ",nativeQuery=true)
	List<Object[]> getDetailLeadTime(Integer productSwCode,String areaId,String userId,Integer switcherCode,LocalDate startDate,LocalDate endDate);
	
	@Query("SELECT count(TL.seqid) FROM TLeadTimeEntity TL JOIN TL.bookingCode P WHERE (?3 IS NULL OR P.productSwCode.productSwCode = ?3) "
			+ "AND P.productSwCode.switcherEntity.switcherCode = ?4 AND TL.status = ?5 AND (?6 IS NULL OR P.userId.userId = ?6) "
			+ "AND (?7 IS NULL OR P.origin = ?7) AND (?8 IS NULL OR P.bookingCode = ?8) AND  P.trxDate between ?1 AND ?2 AND P.productSwCode.isLeadtime=1")
	Integer getTotalLeadTimeByProductSw(LocalDate startDate,LocalDate endDate,Integer productSwCode,Integer switcherCode,String status,String userId,String origin,String bookingCode);
	@Query(value="select tp.booking_code,lt.time_leave,lt.time_arrived " + 
			"from t_lead_time lt " + 
			"join t_payment tp on lt.booking_code = tp.booking_code " + 
			"join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " + 
			"where tp.trx_date between ?1 and ?2 and mp.switcher_code = ?4 and (?3 is null or mp.product_sw_code = ?3) " + 
			"and lt.status = ?5 and (?6 is null or tp.user_id = ?6 ) and (?7 is null or tp.origin = ?7) and (?8 is null or tp.booking_code = ?8)",
			countQuery= "select tp.booking_code,lt.time_leave,lt.time_arrived " + 
						"from t_lead_time lt " + 
						"join t_payment tp on lt.booking_code = tp.booking_code " + 
					    "join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " + 
					    "where tp.trx_date between ?1 and ?2 and mp.switcher_code = ?4 and (?3 is null or mp.product_sw_code = ?3) " + 
						"and lt.status = ?5 and (?6 is null or tp.user_id = ?6 ) and (?7 is null or tp.origin = ?7) and (?8 is null or tp.booking_code = ?8)",nativeQuery=true)
	Page<Object[]> getDetailLeadTimeByProductSw(LocalDate startDate,LocalDate endDate,Integer productSwCode,Integer switcherCode,String status,String userId,String origin,String bookingCode,Pageable pageable);
	
	@Query("SELECT TL FROM TLeadTimeEntity TL JOIN TL.bookingCode P WHERE (?3 IS NULL OR P.productSwCode.productSwCode = ?3) "
			+ "AND P.productSwCode.switcherEntity.switcherCode = ?4 AND TL.status = ?5 AND (?6 IS NULL OR P.userId.userId = ?6) "
			+ "AND (?7 IS NULL OR P.origin = ?7) AND (?8 IS NULL OR P.bookingCode = ?8) AND  P.trxDate between ?1 AND ?2 ")
	Page<TLeadTimeEntity> getDetailLeadTimeBySwitcher(LocalDate startDate,LocalDate endDate,Integer productSwCode,Integer switcherCode,String status,String userId,String origin,String bookingCode,Pageable pageable);
}
