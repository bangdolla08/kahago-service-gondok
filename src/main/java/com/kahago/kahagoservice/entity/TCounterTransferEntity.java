package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "t_counter_transfer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCounterTransferEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private LocalDate trxDate;
    private Integer count;
}
