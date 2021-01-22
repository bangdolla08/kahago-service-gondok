package com.kahago.kahagoservice.model.request;

import lombok.Data;

@Data
public class RegistrationUser {
	private String username;
	private String password;
	private String typeUser;
	private String userLevel;
	private String name;
	private String telp;
	private String areaKonsolidator;
	private String typeDeposit;
	private String creditDay;
	private String userCategory;
	private String branchAccess;
}
