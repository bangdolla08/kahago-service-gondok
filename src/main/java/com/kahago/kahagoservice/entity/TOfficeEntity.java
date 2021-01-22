package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_office")
@Data
public class TOfficeEntity {
    @Id
    @GeneratedValue
    private Long seqid;
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="userId")
    private MUserEntity userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officeCode")
    private MOfficeEntity officeCode;
}
