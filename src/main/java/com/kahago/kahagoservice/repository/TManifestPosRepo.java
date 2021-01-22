package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TManifestPosEntity;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Hendro yuwono
 */
public interface TManifestPosRepo extends PagingAndSortingRepository<TManifestPosEntity, Long> {

    @Procedure(procedureName = "gen_show_payment_pos")
    void callSPWithManifest(@Param("nomanifest") String nomanifest);
}
