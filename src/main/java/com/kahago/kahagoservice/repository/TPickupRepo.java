

package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.entity.TPickupEntity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author bangd ON 27/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TPickupRepo extends JpaRepository<TPickupEntity,Integer> {
	TPickupEntity findByCode(String Code);
	@Query(value="Select t_pickup.* from t_pickup order by code desc limit 1",nativeQuery=true)
	TPickupEntity findTopByIdPickupOrderByIdPickupCourierIdDesc();
	@Query(value = "SELECT T FROM TPickupEntity T WHERE (?1 IS NULL OR T.courierId.userId=?1) AND (COALESCE(?2) IS NULL OR T.status IN (?2)) "
			+ "AND (?3 IS NULL OR T.code LIKE concat('%',?3,'%')) "
			+ "AND (?4 IS NULL OR T.pickupDate = ?4) "
			+ "AND (?5 IS NULL OR T.timePickupId.idPickupTime = ?5) "
			+ "order by T.idPickup desc")
	Page<TPickupEntity> findByCourierIdStatusManifestId(String courierId, List<Integer> status, String manifestId, LocalDate pickupDate,Integer idPickupTime, Pageable pageable);
	
//	@Query(value = "SELECT new com.kahago.kahagoservice.entity.TPickupEntity(T.pickupId) "
//			+ "FROM TPickupDetailEntity T LEFT JOIN T.pickupId PD "
//			+ "WHERE (?1 IS NULL OR PD.courierId.userId=?1) AND (?2 IS NULL OR PD.status=?2) "
//			+ "AND (?3 IS NULL OR PD.code LIKE concat('%',?3,'%') OR T.bookId.bookingCode LIKE concat('%',?3,'%')) Group By PD order by PD.idPickup desc")
//	Page<TPickupEntity> findByCourierIdStatusManifestIdOrBookId(String courierId, Integer status, String manifestId, Pageable pageable);
	TPickupEntity findByCourierIdUserIdAndCode(String userId,String code);

	/**
	 * Untuk Mendapatkan User Yang Masih Memiliki tanggunagan Manifest
	 * @return list Courier yang membawa barang kahago
	 */
	@Query(value = "SELECT TPE.courierId FROM TPickupEntity TPE WHERE TPE.status=0 OR TPE.status=1 GROUP BY TPE.courierId")
	List<MUserEntity> findCourierResponsibility();
	/**
	 * Get Manifest By Data
	 * @return Manifest ByCourier
	 */
	@Query(value = "SELECT TPE FROM TPickupEntity TPE WHERE (TPE.status=0 OR TPE.status=1) AND TPE.courierId.userId=?1 ")
	List<TPickupEntity> findPickupEntitiesByCourier(String userId);
	
	List<TPickupEntity> findByStatusIn(List<Integer> status);
	
	@Query("SELECT PD.pickupId FROM TPickupDetailEntity PD WHERE (?1 IS NULL OR PD.bookId.bookingCode = ?1) AND (?2 IS NULL OR PD.pickupOrderRequestEntity.pickupOrderId = ?2) "
			+ "AND (?3 IS NULL OR PD.pickupId.courierId.userId = ?3) AND (COALESCE(?4) IS NULL OR PD.pickupId.status IN (?4)) "
			+ "AND (?5 IS NULL OR PD.pickupId.pickupDate = ?5) "
			+ "AND (?6 IS NULL OR PD.pickupId.timePickupId.idPickupTime = ?6)")
	Page<TPickupEntity> getManifestByBookingCodeAndPickupOrderId(String bookingCode,String pickupRequest, String courierId, List<Integer> status,LocalDate pikcupDate, Integer idPickupTime,Pageable pageable);
	
	@Query("SELECT DISTINCT PD.pickupId FROM TPickupDetailEntity PD LEFT JOIN PD.bookId P JOIN PD.bookId.userId U WHERE (?1 IS NULL OR U.userId LIKE %?1% ) "
			+ "AND (?2 IS NULL OR PD.pickupId.courierId.userId = ?2) AND (COALESCE(?3) IS NULL OR PD.pickupId.status IN (?3)) "
			+ "AND (?4 IS NULL OR PD.pickupId.pickupDate = ?4) "
			+ "AND (?5 IS NULL OR PD.pickupId.timePickupId.idPickupTime = ?5)")
	Page<TPickupEntity> getManifestByUserId(String manifestId, String courierId, List<Integer> status, LocalDate pickupDate, Integer idPickupTime, Pageable pageable);
	
	Integer countByStatusIn(List<Integer> status);
	
	@Query("SELECT TPE FROM TPickupEntity TPE WHERE TPE.timePickupId.idPickupTime = ?1 AND TPE.courierId.userId = ?2 AND TPE.pickupDate = ?3 ")
	TPickupEntity findByPickupTimeIdAndCourierId(Integer pickupTimeId, String courierId, LocalDate pickupDate);
	@Query("SELECT TD.pickupId FROM TPickupDetailEntity TD LEFT JOIN TD.pickupId T WHERE T.timePickupId.idPickupTime = ?1 "
			+ "AND T.courierId.userId = ?2 AND T.pickupDate = ?3 AND TD.pickupAddrId.postalCode.kecamatanEntity.kotaEntity.areaKotaId=?4")
	List<TPickupEntity> findByPickupTimeIdAndCourierIdAndOrigin(Integer pickupTimeId, String courierId, LocalDate pickupDate,Integer origin);
}

