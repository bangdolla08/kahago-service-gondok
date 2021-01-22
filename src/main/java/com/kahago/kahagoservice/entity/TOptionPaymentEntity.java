package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_option_payment")
@Data
public class TOptionPaymentEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
    @JoinColumn(name = "userCategory")
    private MUserCategoryEntity userCategory;
    private String code;
    private Boolean isDeposit;
    private Boolean isPayment;
    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
    @JoinColumn(name = "optionPaymentId")
    private MOptionPaymentEntity optionPayment;
}
