package com.kahago.kahagoservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MUserLevelEntity;
import com.kahago.kahagoservice.model.response.RoleResp;
import com.kahago.kahagoservice.repository.MUserLevelRepo;

@Service
public class RoleService {

	@Autowired
	private MUserLevelRepo userLevelRepo;
	public List<RoleResp> getRoleResp(){
		return userLevelRepo.findAll().stream().map(this::toRole).collect(Collectors.toList());
	}
	
	private RoleResp toRole(MUserLevelEntity userlevel) {
		return RoleResp.builder()
				.roleId(userlevel.getUserLevel().toString())
				.roleName(userlevel.getLevelName())
				.accountType(userlevel.getAccountType())
				.build();
	}
}
