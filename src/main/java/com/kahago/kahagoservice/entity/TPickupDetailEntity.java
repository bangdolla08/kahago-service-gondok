package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;






import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_pickup_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TPickupDetailEntity {
    @Id
    @GeneratedValue
    private Integer idPickupDetail;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pickupId")
    private TPickupEntity pickupId;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "bookId",updatable=false)
    private TPaymentEntity bookId;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pickupOrderId",updatable=false)
    private TPickupOrderRequestEntity pickupOrderRequestEntity;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="pickupAddrId",updatable=false)
    private TPickupAddressEntity pickupAddrId;
    private Integer status;
    private String pathPic;
    private String createBy;
    private LocalDateTime createDate;
    private String modifyBy;
    private LocalDateTime modifyDate;
    private String reason;
}
