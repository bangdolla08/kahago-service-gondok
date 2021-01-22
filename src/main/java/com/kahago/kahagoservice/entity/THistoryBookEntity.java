package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "t_history_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class THistoryBookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer seq;
    private String bookingCode;
    private String stt;
    private String remarks;
    private String piece;
    private LocalDateTime trxDate;
    private String issuedBy;
    private String updatedBy;
}
