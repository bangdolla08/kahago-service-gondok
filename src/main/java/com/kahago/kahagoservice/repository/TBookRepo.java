package com.kahago.kahagoservice.repository;

/**
 * @author Ibnu Wasis
 */

import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.model.projection.IncomingOfGood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TBookRepo extends JpaRepository<TBookEntity, Integer> {
    List<TBookEntity> findByBookingCode(String bookingCode);

    boolean existsByBookingCodeAndSeqid(String bookingCode, Integer seqId);

    boolean existsByQrCode(String qrCode);

    TBookEntity findByQrCode(String qrCode);

    boolean existsByBookingCode(String bookingCode);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.IncomingOfGood(tb.qrCode, cust.name, courier.name, tb.length, " +
            "tb.width, tb.height, tb.volWeight, tb.grossWeight, prodswitcher.displayName, switcher.displayName, tb.status, cast(true as boolean), tp.bookingCode) " +
            "FROM TPickupDetailEntity tpd LEFT JOIN " +
            "tpd.bookId tp LEFT JOIN " +
            "tp.tbooks tb LEFT JOIN " +
            "tp.userId cust LEFT JOIN " +
            "tpd.pickupId pick LEFT JOIN " +
            "pick.courierId courier LEFT JOIN " +
            "tp.productSwCode prodswitcher LEFT JOIN " +
            "prodswitcher.switcherEntity switcher LEFT JOIN " +
            "tpd.pickupAddrId tpa LEFT JOIN " +
            "tpa.postalCode mpc LEFT JOIN " +
            "mpc.kecamatanEntity mad LEFT JOIN " +
            "mad.kotaEntity mak " +
            "WHERE tb.status = ?1 AND tpd.status = 1 AND mak.areaKotaId IN ?2")
    List<IncomingOfGood> findByStatusAndCityIds(int status, Set<Integer> cityIds);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.IncomingOfGood(tb.qrCode, cust.name, courier.name, tb.length, " +
            "tb.width, tb.height, tb.volWeight, tb.grossWeight, prodswitcher.displayName, switcher.displayName, tb.status, cast(true as boolean), tp.bookingCode) " +
            "FROM TPickupDetailEntity tpd LEFT JOIN " +
            "tpd.bookId tp LEFT JOIN " +
            "tp.tbooks tb LEFT JOIN " +
            "tp.userId cust LEFT JOIN " +
            "tpd.pickupId pick LEFT JOIN " +
            "pick.courierId courier LEFT JOIN " +
            "tp.productSwCode prodswitcher LEFT JOIN " +
            "prodswitcher.switcherEntity switcher LEFT JOIN " +
            "tpd.pickupAddrId tpa LEFT JOIN " +
            "tpa.postalCode mpc LEFT JOIN " +
            "mpc.kecamatanEntity mad LEFT JOIN " +
            "mad.kotaEntity mak " +
            "WHERE tb.status = ?1 AND tpd.status = 1 AND mak.areaKotaId IN ?2 AND tb.qrCode = ?3")
    IncomingOfGood findByStatusAndCityIdsAndQrCode(int status, Set<Integer> cityIds, String qrCode);
}
