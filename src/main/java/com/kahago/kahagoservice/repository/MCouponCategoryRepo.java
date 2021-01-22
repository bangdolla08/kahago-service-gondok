package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MCouponCategoryUserEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 16 Des 2019
 */
public interface MCouponCategoryRepo extends PagingAndSortingRepository<MCouponCategoryUserEntity, Integer> {
	@Query("SELECT C FROM MCouponCategoryUserEntity C "
			+ "LEFT JOIN C.idCategoryUser U "
			+ "WHERE C.idCoupon=?1 AND U.seqid=?2")
	List<MCouponCategoryUserEntity> findByIdCategoryUserAndUserEntity(Integer id,Integer userid);
	List<MCouponCategoryUserEntity> findAllByIdCoupon(Integer idCoupon);
	@Modifying
	@Query(value="insert into m_coupon_category_user (id_coupon,id_category_user) values (?1,?2)",nativeQuery=true)
	void insertToMuserCategory(Integer idCoupon,Integer idCategory);
	
	@Modifying
	@Query(value="delete from m_coupon_category_user where id_coupon=?1",nativeQuery=true)
	void deleteByIdCoupon(Integer idCoupon);
}