package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "t_warehouse_receive_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TWarehouseReceiveDetailEntity {

    @Id
    @GeneratedValue
    private Integer idWarehouseReceiveDetail;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseReceiveId")
    private TWarehouseReceiveEntity warehouseReceiveId;
    private Integer pickupDetailId;
    @OneToOne
    @JoinColumn(name="bookId")
    private TPaymentEntity bookId;
    private Integer status;
    private String approvalRejectBy;
    private LocalDateTime approvalRejectAt;
    private String cancelBookingBy;
    private LocalDateTime cancelBookingAt;
    private String createBy;
    private LocalDateTime createAt;
    private String updateBy;
    private LocalDateTime updateAt;
    private String qrcodeRequest;
    private String reason;
//    @OneToOne(cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "qrcodeRequest",updatable = false,insertable = false, referencedColumnName = "qrcodeExt" )
//    private TPickupOrderRequestDetailEntity pickupOrderRequestDetailEntity;
//    private String qrcodeRequest;
}
