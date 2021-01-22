package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MGoodsEntity;
import com.kahago.kahagoservice.model.response.GoodsRes;
import com.kahago.kahagoservice.repository.MGoodsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Service
public class GoodsService {

    @Autowired
    private MGoodsRepo goodsRepo;

    public List<GoodsRes> findAllGoods() {
        return goodsRepo.findAll().stream().map(this::toDtoResponse).collect(Collectors.toList());
    }
    public List<GoodsRes> findAllGoods(String productCode) {
        return goodsRepo.findAllByProductCode(Long.parseLong(productCode)).stream().map(this::toDtoResponse).collect(Collectors.toList());
    }

    private GoodsRes toDtoResponse(MGoodsEntity entity) {
        return GoodsRes.builder()
                .description(entity.getDescription()==null?"":entity.getDescription())
                .goodsName(entity.getGoodsName())
                .id(String.valueOf(entity.getGoodsId()))
                .insuranceFlag(entity.getInsuranceFlag())
                .insuranceValue(entity.getInsuranceValue().toString())
                .packFlag(entity.getPackFlag())
                .packValue(entity.getPackValue())
                .build();
    }
}
