package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Table(name = "t_payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TPaymentHistoryEntity {
    @Id
    @GeneratedValue
    private Long seqid;
    @ManyToOne
    @JoinColumn(name="bookingCode")
    private TPaymentEntity bookingCode;
    private String userId;
    private BigDecimal amount;
    private Timestamp trxServer;
    private Integer shippingSurcharge;
    private Integer lastShippingSurcharge;
    private BigDecimal insurance;
    private Integer lastInsurance;
    private BigDecimal extraCharge;
    private Integer lastExtraCharge;
    private Integer jumlahLembar;
    private Integer status;
    private String origin;
    private String destination;
    private Long grossWeight;
    private Long volume;
    private String comodity;
    private String note;
    private String goodsDesc;
    private BigDecimal priceKg;
    private BigDecimal lastPriceKg;
    private Integer pickupAddrId;
    private String serviceType;
    private BigDecimal price;
    private Integer totalPackKg;
    private BigDecimal priceRepack;
    private BigDecimal lastPriceRepack;
    private Integer totalHpp;
    private Integer profit;
    private String keterangan;
    private String lastUser;
    private LocalDateTime lastUpdate;
    private BigDecimal lastPrice;
    private BigDecimal lastAmount;
    private Long lastGrossWeight;
    private Long lastVolume;
    private Integer lastStatus;
    private String reason;
    private Integer isRefund;
}
