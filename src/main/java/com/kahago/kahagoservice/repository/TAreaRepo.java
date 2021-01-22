package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MAreaDetailEntity;
import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TAreaRepo extends JpaRepository<TAreaEntity,Long> {


    /**
     *
     * @param areaOriginId
     * @param areaId
     * @param goodsId
     * @param limitMinimum
     * @return
     */
    @Query(value = "SELECT ta.* FROM " +
            "t_area AS ta INNER JOIN " +
            "m_postal_code AS mp ON ta.area_id = mp.id_postal_code INNER JOIN " +
            "m_product_switcher AS productswicher ON ta.product_sw_code = productswicher.product_sw_code INNER JOIN " +
//			"m_product_switcher AS productswicher ON ta.product_switcher = productswicher.name INNER JOIN " +
            "m_switcher AS switcher ON productswicher.switcher_code = switcher.switcher_code INNER JOIN t_goods ON productswicher.product_sw_code = t_goods.product_sw_code WHERE " +
            "(?3 IS NULL OR t_goods.goods_id = ?3) AND " +
            "(curdate( ) NOT BETWEEN productswicher.libur_start AND productswicher.libur_end OR productswicher.libur_start IS NULL) AND " +
            "(mp.area_detail_id = ?2) AND ta.status='1' AND " +
            "ta.area_origin_id = ?1 AND productswicher.status = 0 AND " +
            "ta.tarif > 0 AND ta.limit_minimum<=?4 AND ta.status = '1' GROUP BY  " +
            "ta.area_id ,ta.product_sw_code " +
            "ORDER BY ta.tarif ASC ," +
            "productswicher.priority_seq ASC",nativeQuery = true)
    List<TAreaEntity> searchTarif(String areaOriginId,Integer areaId,Long goodsId,Integer limitMinimum);

    /**
     * Get Width userCategory
     * @param areaOriginId
     * @param areaId
     * @param goodsId
     * @param limitMinimum
     * @param userCategoryId
     * @return
     */
    @Query(value = "SELECT ta.* FROM " +
            "t_area AS ta INNER JOIN " +
            "m_postal_code AS mp ON ta.area_id = mp.id_postal_code INNER JOIN " +
            "m_product_switcher AS productswicher ON ta.product_sw_code = productswicher.product_sw_code INNER JOIN " +
            "m_switcher AS switcher ON productswicher.switcher_code = switcher.switcher_code INNER JOIN " +
            "t_goods ON productswicher.product_sw_code = t_goods.product_sw_code " +
            "INNER JOIN t_category_switcher ON switcher.switcher_code = t_category_switcher.switcher_code AND t_category_switcher.id_user_category=?5 WHERE " +
            "(?3 IS NULL OR t_goods.goods_id = ?3) AND " +
            "(curdate( ) NOT BETWEEN productswicher.libur_start AND productswicher.libur_end OR productswicher.libur_start IS NULL) AND " +
            "(mp.area_detail_id = ?2) AND " +
            "ta.area_origin_id = ?1 AND ta.status='1' AND " +
            "ta.tarif > 0 AND ta.limit_minimum<=?4 AND productswicher.status = 0 GROUP BY  " +
            "ta.area_id ,ta.product_sw_code " +
            "ORDER BY ta.tarif ASC ," +
            "productswicher.priority_seq ASC",nativeQuery = true)
    List<TAreaEntity> searchTarif(String areaOriginId,Integer areaId,Long goodsId,Integer limitMinimum,Integer userCategoryId);
    
    

    
    /**
     * To Get Single Price
     * @param productSwCode
     * @param areaId
     * @return Single Price
     */
    List<TAreaEntity> findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(MProductSwitcherEntity productSwCode,MPostalCodeEntity areaId,String areaOriginId);
    @Query("SELECT A FROM TAreaEntity A WHERE A.productSwCode=?1 AND A.areaId.kecamatanEntity=?2 "
    		+ "AND A.areaOriginId=?3  ")
    List<TAreaEntity> findTOPByProductSwCodeAndMPostalCodeAndAreaOriginIdOrderByTarifAsc(MProductSwitcherEntity productSwCode,MAreaDetailEntity kecamatan,String areaOriginId);
    
    @Query(value="SELECT T FROM TAreaEntity T "
    		+ "WHERE T.areaId=?1 AND T.vendor=?2 AND T.areaOriginId=?3")
    List<TAreaEntity> findByAreaIdAndVendor(MPostalCodeEntity areaId,MSwitcherEntity vendor,String areaOriginId);
    
    List<TAreaEntity> findTOPByProductSwCodeAndAreaIdOrderByTarifAsc(MProductSwitcherEntity productSwCode,MPostalCodeEntity postalCodeEntity);
//    @Query(value = "SELECT T FROM TAreaEntity  T WHERE T.areaId.kecamatanEntity.areaDetailId=?1 AND  ")
//    TAreaEntity findByAreaDetailIdAndProductSwCode(Integer areaDetailId,Integer productSwCode);
    TAreaEntity findTopByAreaIdKecamatanEntityAreaDetailIdAndProductSwCodeProductSwCode(Integer areaDetailId,Long ProductSwCode);    

	List<TAreaEntity> findTopByProductSwCodeProductSwCodeAndAreaIdIdPostalCodeAndAreaOriginIdOrderByTarifAsc(Long productSwCode,Integer idPostalCode,String areaOriginId);
	
	@Query("SELECT T FROM TAreaEntity T WHERE T.lastUpdate < ?1 And T.status = 1")
    Page<TAreaEntity> findByMoreDat(LocalDateTime localTimeFrom, Pageable page);    
	@Query("SELECT count(T.seqid) FROM TAreaEntity T WHERE T.status = 1 and T.lastUpdate < ?1 ")
//	@Query(value="SELECT COUNT(a.seqid) FROM t_area a inner join m_vendor_area av "
//			+ "on av.postal_code_id=a.area_id and av.switcher_code=a.switcher_code "
//			+ "where a.status=1 and a.last_update < ?1",
//			nativeQuery = true)
    Integer countPostalCode(LocalDateTime localTimeFrom);    
	@Procedure(procedureName = "update_area")
    void callSPDiff();
    
    List<TAreaEntity> findBySeqidIn(List<Long> seqid);
    
}
