package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "m_receiver")
@Data
@Builder
@AllArgsConstructor
public class MReceiverEntity {
    @Id
    @GeneratedValue
    private Integer receiverId;
    private String receiverName;
    private String receiverAddress;
    private String receiverTelp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPostalCode")
    private MPostalCodeEntity idPostalCode;
    private String receiverEmail;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId")
    private MUserEntity userId;
    private String createdBy;
    private LocalDate createdDate;
    private String updatedBy;
    private LocalDate updatedDate;
    private Integer status;
    public MReceiverEntity(){

    }
}
