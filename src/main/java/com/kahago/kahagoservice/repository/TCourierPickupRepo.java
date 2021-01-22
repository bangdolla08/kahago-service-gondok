package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TCourierPickupEntity;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Hendro yuwono
 */
@Repository
public interface TCourierPickupRepo extends JpaRepository<TCourierPickupEntity, Integer> {
    List<TCourierPickupEntity> findByPickupIdPickupAndStatus(int pickupId, int status);

    Stream<TCourierPickupEntity> findByPickupIdPickupAndStatusIn(int pickupId, List<Integer> status);

    List<TCourierPickupEntity> findByPickupIdPickupAndPickupAddressPickupAddrIdIn(int pickupId, List<Integer> listOfPickupAddress);

    List<TCourierPickupEntity> findByIdIn(List<Integer> ids);

    List<TCourierPickupEntity> findByStatusAndCourierId(Integer status, String courierId);

    @Query("SELECT cp FROM TCourierPickupEntity cp LEFT JOIN FETCH " +
            "cp.pickup p LEFT JOIN FETCH " +
            "cp.pickupAddress pa " +
            "WHERE p.idPickup = ?1 AND pa.pickupAddrId = ?2")
    TCourierPickupEntity findByPickupIdAndPickupAddressId(Integer pickupId, Integer pickupAddrId);

    boolean existsByPickupIdPickup(int pickupId);

    boolean existsByCourierIdAndStatusIn(String courierId, List<Integer> status);

    List<TCourierPickupEntity> findByPickupIdPickup(Integer pickupId);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.status IN ?1 AND cp.courierId = ?2 " +
            "ORDER BY p.pickupDate ASC, p.timePickupFrom ASC")
    Page<PickupCourier> findByStatusInAndCourierId(List<Integer> status, String courierId, Pageable pageable);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "p.timePickupId pt LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.status IN ?1 AND pt.idPickupTime IN ?2 AND cp.courierId = ?3 " +
            "ORDER BY p.pickupDate ASC, p.timePickupFrom ASC")
    Page<PickupCourier> findByStatusTimePickupAndCourierId(List<Integer> status, List<Integer> pickupTimeIds, String courierId, Pageable pageable);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.status IN ?1 " +
            "AND (UPPER(u.name) LIKE %?3% OR UPPER(u.hp) LIKE %?3% OR UPPER(pa.address) LIKE %?3% OR UPPER(pa.description) LIKE %?3% OR UPPER(p.code) LIKE %?3%) " +
            "AND cp.courierId = ?2 " +
            "ORDER BY p.pickupDate ASC, p.timePickupFrom ASC")
    Page<PickupCourier> findByStatusCourierIdAndTerm(List<Integer> status, String courierId, String term, Pageable pageable);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.status IN ?1 AND cp.courierId = ?2 ORDER BY cp.id DESC")
    PickupCourier findByStatusInAndCourierId(List<Integer> status, String courierId);

    boolean existsByStatusAndCourierId(Integer status, String courierId);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.id = ?1")
    PickupCourier findByIds(Integer id);

    @Query("SELECT u.userId FROM TCourierPickupEntity cp LEFT JOIN cp.pickupAddress pa LEFT JOIN pa.userId u WHERE cp.id = ?1")
    String findCustomerIdById(Integer id);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.id = ?1")
    PickupCourier findByIdPickupCourier(Integer id);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupCourier(cp.id, p.idPickup, cp.status, " +
            "p.timePickupFrom, p.timePickupTo, p.pickupDate, " +
            "p.code, u.name, u.hp, pa.pickupAddrId, pa.address, pa.description, pa.latitude, pa.longitude, pa.flag) " +
            "FROM TCourierPickupEntity cp LEFT JOIN  " +
            "cp.pickup p LEFT JOIN " +
            "cp.pickupAddress pa LEFT JOIN " +
            "pa.userId u " +
            "WHERE cp.courierId = ?1 AND cp.id IN ?2")
    List<PickupCourier> findByCourierAndIds(String courierId, List<Integer> ids);

    boolean existsByIdAndCourierId(Integer id, String courierId);
}
