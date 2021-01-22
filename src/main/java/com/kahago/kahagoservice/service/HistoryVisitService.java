package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.entity.THistoryVisitEntity;
import com.kahago.kahagoservice.model.request.HistoryVisitRequest;
import com.kahago.kahagoservice.model.response.HistoryVisitResponse;
import com.kahago.kahagoservice.repository.THistoryVisitRepo;

import java.util.List;

/**
 * @author Ibnu Wasis
 */
@Service
public class HistoryVisitService {
    @Autowired
    private THistoryVisitRepo tHistoryVisitRepo;

    @Transactional
    public HistoryVisitResponse getHistoryVisitingMenu(HistoryVisitRequest request, String userId) {
        request.setUserId(userId);
        THistoryVisitEntity entity = tHistoryVisitRepo.findByUserId(userId);
        THistoryVisitEntity entities = tHistoryVisitRepo.findByParamAndUrlAndActionAndUserIdNotAndFlag(request.getParam(), request.getUrl(), request.getAction(), userId,1);

        if (entity == null) {
            entity = new THistoryVisitEntity();
            entity.setUrl(request.getUrl());
            entity.setParam(request.getParam());
            entity.setAction(request.getAction());
            entity.setUserId(request.getUserId());
            entity.setFlag(request.getFlag());
        } else if (entities == null) {
            if (entity.getFlag().equals(0)) {
                entity.setUrl(request.getUrl());
                entity.setParam(request.getParam());
                entity.setAction(request.getAction());
                entity.setUserId(request.getUserId());
                entity.setFlag(request.getFlag());
            } else if (entity.getUserId().equals(request.getUserId()) &&
                    entity.getParam().equals(request.getParam()) &&
                    entity.getUrl().equals(request.getUrl())) {
                entity.setUrl(request.getUrl());
                entity.setParam(request.getParam());
                entity.setAction(request.getAction());
                entity.setUserId(request.getUserId());
                entity.setFlag(request.getFlag());
            }
        } else {
            entity = entities;
        }
        tHistoryVisitRepo.save(entity);
        HistoryVisitResponse response = HistoryVisitResponse.builder()
                .url(entity.getUrl())
                .param(entity.getParam())
                .action(entity.getAction())
                .flag(entity.getFlag())
                .userId(entity.getUserId())
                .build();
        return response;
    }
}
