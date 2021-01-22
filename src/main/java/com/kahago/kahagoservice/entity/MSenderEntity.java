package com.kahago.kahagoservice.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "m_sender")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MSenderEntity {
    @Id
    @GeneratedValue
    private Integer senderId;
    private String senderName;
    private String senderAddress;
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "userId")
    private MUserEntity userId;
    private String senderTelp;
    private String senderEmail;
    private String createdBy;
    private LocalDate createdDate;
    private String updatedBy;
    private LocalDate updatedDate;
    private Integer status;
}
