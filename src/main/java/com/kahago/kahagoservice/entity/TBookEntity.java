package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
@Table(name = "t_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TBookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer seqid;
    @Column(name="booking_code")
    private String bookingCode;
    private String length;
    private String width;
    private String height;
    private String grossWeight;
    private String volWeight;
    private LocalDateTime tglSystem;
    private String qrCode;
    private Integer status;
    private String images;
}
