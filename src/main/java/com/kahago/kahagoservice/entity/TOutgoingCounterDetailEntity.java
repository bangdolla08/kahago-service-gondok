package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "t_outgoing_counter_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TOutgoingCounterDetailEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    @ManyToOne
    @JoinColumn(name="outgoingCounterId")
    private TOutgoingCounterEntity outgoingCounterId;
    @OneToOne
    @JoinColumn(name = "bookingCode")
    private TPaymentEntity bookingCode;
    private Integer status;
    private LocalDate updateDate;
    private String updateBy;
}
