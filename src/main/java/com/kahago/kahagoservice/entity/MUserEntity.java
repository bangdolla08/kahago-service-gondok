package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "m_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MUserEntity {
    @Id
    private String userId;
    private String password;
    private Integer userLevel;
    private String accountNo;
    private String statusLogin;
    private String statusLayanan;
    private String name;
    private LocalDateTime expPassword;
    private String pob;
    private String dob;
    private String idType;
    private String idNo;
    private String email;
    private String sex;
    private String accountType;
    private String printerType;
    private String areaOriginId;
    private String printerMethode;
    private String registerUser;
    private LocalDateTime registerDate;
    private String lastUser;
    private LocalDateTime lastUpdate;
    private Long groupMenuCode;
    private String hp;
    private String sessionId;
    private String passSession;
    private String addr;
    private String idPostalCode;
    private String accountStatus;
    private LocalDateTime lastTimeSession;
    private BigDecimal balance;
    private String depositType;
    private String creditDay;
    private String noRekening;
    private String bankCode;
    private String namaRekening;
    private String refNum;
    private Integer courierFlag;
    private Integer qtyDeposit;
    @ManyToOne
    @JoinColumn(name = "user_category")
    private MUserCategoryEntity userCategory;
    private String tokenNotif;
    private Integer mitraFlag;
    private String danaOauth;
    private String danaState;
    private Integer isBranch;
}
