package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MPermohonanEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPermohonanEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 8 Jun 2020
 */
@Repository
public interface TPermohonanRepo extends JpaRepository<TPermohonanEntity,Integer> {
	List<TPermohonanEntity> findByNomorPermohonan(MPermohonanEntity nomorPermohonan);
	List<TPermohonanEntity> findByNomorPermohonanAndStatusIn(MPermohonanEntity nomorPermohonan,List<Integer> status);
	@Query("SELECT T FROM TPermohonanEntity T WHERE T.bookingCode=?1 and T.nomorPermohonan.nomorPermohonan <> ?2")
	List<TPermohonanEntity> findByBookingCodeAndPermohonan(TPaymentEntity bookingCode,String permohonan);
	@Query("SELECT T FROM TPermohonanEntity T WHERE T.bookingCode=?1 And T.noInv <> ?2 And T.status > -1")
	List<TPermohonanEntity> findByBookingCodeAndNoInvoice(TPaymentEntity bookingCode,String noinv);
	TPermohonanEntity findByBookingCodeAndNoInv(TPaymentEntity bookingCode,String noInv);
	
	Optional<TPermohonanEntity> findBySeqidAndBookingCodeAndNoInv(Integer seqid,TPaymentEntity bookingCode,String noInv);
	
	List<TPermohonanEntity> findAllByBookingCodeAndBookingCodeStatusIn(TPaymentEntity payment,List<Integer> status);
	@Query("SELECT T.nomorPermohonan FROM TPermohonanEntity T "
			+ "WHERE (COALESCE(?1) IS NULL OR T.nomorPermohonan.status IN (?1)) "
			+ "And (COALESCE(?2) IS NULL OR T.bookingCode.productSwCode.switcherEntity.switcherCode IN (?2)) "
			+ "AND (?3 IS NULL OR T.nomorPermohonan.nomorPermohonan=?3) "
			+ "ANd (?4 IS NULL OR T.bookingCode.bookingCode=?4 OR T.bookingCode.stt=?4) Group By T.nomorPermohonan Order By T.nomorPermohonan Desc")
	Page<MPermohonanEntity> findAllByStatusAndVendorAndPermohonanAndBookId(List<Integer> status,List<Integer> vendor,String noPermohonan,String bookId,Pageable pageable);
	
	List<TPermohonanEntity> findByBookingCodeBookingCodeIn(List<String> bookingCode);
	
	@Query("SELECT T FROM TPermohonanEntity T WHERE T.nomorPermohonan.nomorPermohonan=?1 and T.bookingCode.bookingCode=?2")
	TPermohonanEntity findByNoPermohonanNoPermohonanAndBookingCodeBookingCode(String nopermohonan,String bookingCode);
	@Query("SELECT T FROM TPermohonanEntity T WHERE T.nomorPermohonan.nomorPermohonan=?1 "
			+ "AND T.bookingCode.bookingCode=?2 AND T.noInv=?3")
	Optional<TPermohonanEntity> findByApprovalReject(String noPermohonan,String bookingCode,String noInv);
	@Query("SELECT T FROM TPermohonanEntity T "
			+ "WHERE (COALESCE(?1) IS NULL OR T.nomorPermohonan.status IN (?1)) "
			+ "And (COALESCE(?2) IS NULL OR T.bookingCode.productSwCode.switcherEntity.switcherCode IN (?2)) "
			+ "AND (?3 IS NULL OR T.nomorPermohonan.nomorPermohonan=?3) "
			+ "ANd (?4 IS NULL OR T.bookingCode.bookingCode=?4 OR T.bookingCode.stt=?4)")
	Page<TPermohonanEntity> findAllByStatusPermohonan(List<Integer> status,List<Integer> vendor,String noPermohonan,String bookId,Pageable pageable);
	
	@Query("SELECT T FROM TPermohonanEntity T "
			+ "WHERE (COALESCE(?1) IS NULL OR T.nomorPermohonan.status IN (?1)) "
			+ "And (COALESCE(?2) IS NULL OR T.bookingCode.productSwCode.switcherEntity.switcherCode IN (?2)) "
			+ "AND (?3 IS NULL OR T.nomorPermohonan.nomorPermohonan=?3) "
			+ "ANd (?4 IS NULL OR T.bookingCode.bookingCode=?4 OR T.bookingCode.stt=?4) And T.status=?5 Order by T.bookingCode.bookingCode ASC")
	Page<TPermohonanEntity> findAllByStatusPermohonan(List<Integer> status,List<Integer> vendor,String noPermohonan,String bookId,Integer statusDetail,Pageable pageable);
	
	@Query("SELECT count(T) FROM TPermohonanEntity T WHERE T.nomorPermohonan=?1 AND T.status=?2")
	Long countByNomorPermohonanAndStatus(MPermohonanEntity permohonan,Integer status);
	
}
