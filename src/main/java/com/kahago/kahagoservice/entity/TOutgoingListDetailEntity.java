package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "t_outgoing_list_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TOutgoingListDetailEntity {
    @Id
    @GeneratedValue
    private Integer idOutgoingListDetail;
    private Integer outgoingListId;
    private Integer warehouseReceiveDetailId;
    @OneToOne
    @JoinColumn(name = "bookingCode")
    private TPaymentEntity bookingCode;
    private String tisCable;
}
