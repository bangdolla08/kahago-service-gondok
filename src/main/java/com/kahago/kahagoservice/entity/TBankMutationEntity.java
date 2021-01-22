package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_bank_mutation")
@Data
public class TBankMutationEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String bankAccount;
    private Integer typeTrx;
    private String typeMutation;
    private Long amount;
    private String description;
    private Long balance;
    private Timestamp created;
    private Timestamp modified;
    private String userCreated;
    private String userModified;
}
