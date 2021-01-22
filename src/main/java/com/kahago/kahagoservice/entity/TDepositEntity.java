package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_deposit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TDepositEntity {
    @Id
    private String tiketNo;
//    private String userId;
    @ManyToOne()
    @JoinColumn(name = "userId")
    private MUserEntity userId;
    private BigDecimal nominal;
    private Integer status;
    private LocalDateTime trxServer;
    private LocalDateTime trxRequest;
    private LocalDateTime trxApprove;
    private String lastUser;
    private LocalDateTime lastUpdate;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bankDepCode")
    private MBankDepositEntity bankDepCode;
    private String userApprove;
    private LocalDateTime trxKonfirmasi;
    private Integer nominalApproval;
    private Integer insufficientFund;
    private String description;
    private Byte isConfirmTransfer;
    private String idPayment;
    private String idTicket;
    private Integer countPawoon;
}
