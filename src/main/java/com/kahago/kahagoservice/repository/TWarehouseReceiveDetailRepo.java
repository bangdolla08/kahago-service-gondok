package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TWarehouseReceiveDetailEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TWarehouseReceiveDetailRepo extends JpaRepository<TWarehouseReceiveDetailEntity, Integer> {
	@Query("select T from TWarehouseReceiveDetailEntity T where T.bookId.bookingCode = ?1 and T.status =?2")
    TWarehouseReceiveDetailEntity findFirstByBookIdAndStatus(String bookingCode,Integer status);
    List<TWarehouseReceiveDetailEntity> findAllByStatusOrderByBookIdDesc(Integer status);
    @Query("select W from TWarehouseReceiveDetailEntity W where W.warehouseReceiveId.officeCode=?3 AND W.status = ?1 and (?2 IS NULL OR (W.bookId.bookingCode LIKE CONCAT('%',?2,'%') or W.bookId.qrcode LIKE CONCAT('%',?2,'%') or W.bookId.qrcodeExt LIKE CONCAT('%',?2,'%'))) AND (?4 IS NULL OR W.bookId.userId.userId LIKE CONCAT('%',?4,'%') ) AND (?5 IS NULL OR W.bookId.productSwCode.switcherEntity.switcherCode=?5) AND W.bookId.status=?6")
    Page<TWarehouseReceiveDetailEntity> findAllByStatusAndBookIdOrQrcodeOrQrcodeext(Integer status, String bookId, String officeCode,String userBook,Integer switcherCode,Integer statusPay, Pageable pageable);
    
    @Query("select W from TWarehouseReceiveDetailEntity W where W.warehouseReceiveId.officeCode IN (?3) "
    		+ "AND W.status = ?1 and (?2 IS NULL OR (W.bookId.bookingCode LIKE CONCAT('%',?2,'%') or "
    		+ "W.bookId.qrcode LIKE CONCAT('%',?2,'%') or W.bookId.qrcodeExt LIKE CONCAT('%',?2,'%'))) "
    		+ "AND (?4 IS NULL OR W.bookId.userId.userId LIKE CONCAT('%',?4,'%') ) "
    		+ "AND (COALESCE(?5) IS NULL OR W.bookId.productSwCode.switcherEntity.switcherCode IN ?5) "
    		+ "AND W.bookId.status=?6 "
    		+ "AND (?7 IS NULL OR W.bookId.userId LIKE CONCAT('%',?7,'%') OR W.bookId.stt LIKE CONCAT('%',?7,'%') OR W.bookId.receiverAddress LIKE CONCAT('%',?7,'%') "
			+ "OR W.bookId.receiverTelp LIKE CONCAT('%',?7,'%') "
			+ "OR W.bookId.receiverName LIKE CONCAT('%',?7,'%') OR W.bookId.receiverEmail LIKE CONCAT('%',?7,'%') OR W.bookId.senderAddress LIKE CONCAT('%',?7,'%') "
			+ "OR W.bookId.senderTelp LIKE CONCAT('%',?7,'%'))  ")
    Page<TWarehouseReceiveDetailEntity> findAllByStatusAndBookIdOrQrcodeOrQrcodeextAndOfficeCodeIN(Integer status, String bookId, List<String> officeCode,String userBook,List<Integer> switcherCode,Integer statusPay,String filter, Pageable pageable);

    @Query("select W from TWarehouseReceiveDetailEntity W where W.status IN (?1) and W.warehouseReceiveId.officeCode=?3 AND  (W.bookId.bookingCode =?2 or W.bookId.qrcode =?2 or W.bookId.qrcodeExt =?2 ) AND W.bookId.status IN (?4)")
    TWarehouseReceiveDetailEntity findByStatusAndBookIdOrQrcodeOrQrcodeext(List<Integer> status,String bookId,String officeCode,List<Integer> statusPay);
    @Query("select W from TWarehouseReceiveDetailEntity W where W.warehouseReceiveId.officeCode = ?5 and (?1 is null or W.bookId.bookingCode = ?1) "
    		+ "and (?2 is null or W.bookId.userId.userId = ?2) and (?3 is null or W.bookId.productSwCode.switcherEntity.switcherCode = ?3) "
    		+ "and W.bookId.status IN (?4) order by W.bookId")
    Page<TWarehouseReceiveDetailEntity> findAllByFilter(String bookId,String userId,Integer vendorId,List<Integer> status,String officeCode,Pageable pageable);
    
    TWarehouseReceiveDetailEntity findByIdWarehouseReceiveDetail(Integer idWarehouseReceiveDetail);
    
    Optional<TWarehouseReceiveDetailEntity> findByQrcodeRequest(String qrcodeRequest);
    Optional<TWarehouseReceiveDetailEntity> findByQrcodeRequestAndStatus(String qrcodeRequest,Integer status);
    Boolean existsByQrcodeRequestAndStatus(String qrcode, Integer status);
    
    TWarehouseReceiveDetailEntity findByBookId(TPaymentEntity bookId);
}
