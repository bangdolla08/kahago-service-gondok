package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Entity
@Table(name = "t_book_detail_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TBookDetailHistoryEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private String bookingCode;
    private String length;
    private String width;
    private String height;
    private String grossWeight;
    private String volWeight;
    private LocalDateTime tglSystem;
    private String counterChanges;
    private String lastLength;
    private String lastWidth;
    private String lastHeight;
    private String lastGrossWeight;
    private String lastVolWeight;
    private String updateUserid;
    @ManyToOne
    @JoinColumn(name = "paymentHistoryId")
    private TPaymentHistoryEntity paymentHistory;

}
