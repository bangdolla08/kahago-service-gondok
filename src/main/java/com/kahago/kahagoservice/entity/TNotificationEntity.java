package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "t_notification")
@Data
public class TNotificationEntity {
    @Id
    private Long seqid;
    private String trxNo;
    private String statusTrx;
    private String note;
    private Timestamp tglTrx;
}
