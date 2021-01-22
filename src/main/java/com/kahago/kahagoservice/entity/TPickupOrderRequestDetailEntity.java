package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.entity
 */
@Data
@Entity
@Table(name = "t_pickup_order_request_detail")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TPickupOrderRequestDetailEntity {
    @Id
    @GeneratedValue
    private Integer seq;
    @ManyToOne
    @JoinColumn(name = "pickupOrderId")
    private TPickupOrderRequestEntity orderRequestEntity;
    @ManyToOne
    @JoinColumn(name = "productSwCode")
    private MProductSwitcherEntity productSwitcherEntity;
    @ManyToOne
    @JoinColumn(name="areaId")
    private MAreaDetailEntity areaId;
    private String namaPenerima;
    private Integer qty;
    private Double weight;
    private String qrCode;
    private Integer status;
    private String bookCode;
    private Integer isPay;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    private String idPayment;
    private String idTicket;
    private BigDecimal amount;
    private String qrcodeExt;
    private String noTiket;
    private BigDecimal amountUniq;
    private String paymentOption;
    private Integer countPawoon;
//    @OneToOne(mappedBy = "pickupOrderRequestDetailEntity")
//    private TWarehouseReceiveDetailEntity warehouseDetailEntity;
    private String pathPic;
}
