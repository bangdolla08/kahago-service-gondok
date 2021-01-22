package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.UserCategoryRequest;
import com.kahago.kahagoservice.model.response.UserCategoryResponse;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserPriorityRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Service
public class CategoryUserService {

    @Autowired
    private MUserCategoryRepo userCategoryRepo;

    public List<UserCategoryResponse> findAll() {
        return userCategoryRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void save(UserCategoryRequest request) {
        MUserCategoryEntity entity = MUserCategoryEntity.builder()
                .accountType(request.getAccountType().equals("internal") ? 1 : 2)
                .lastUpdate(LocalDateTime.now())
                .lastUser("system")
                .roleName(request.getRoleName().toUpperCase()).build();
        userCategoryRepo.save(entity);
    }
    private UserCategoryResponse toDto(MUserCategoryEntity mUserCategoryEntity){
        return CategoryUserService.toDtoUserCategory(mUserCategoryEntity);
    }
    public static UserCategoryResponse toDtoUserCategory(MUserCategoryEntity entity) {
        UserCategoryResponse response = new UserCategoryResponse();
        response.setAccountType(entity.getAccountType());
        response.setNameCategory(entity.getNameCategory());
        response.setRoleName(entity.getRoleName());
        response.setId(entity.getSeqid());
        return response;
    }

    public void delete(Integer id) {
        MUserCategoryEntity entity = userCategoryRepo.findById(id).orElseThrow(() -> new NotFoundException("Id not found"));
        userCategoryRepo.deleteById(entity.getSeqid());
    }
}
