package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.entity
 */
@Data
@Table(name = "t_pickup_order_request")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TPickupOrderRequestEntity {
    @Id
    private String pickupOrderId;
    @ManyToOne
    @JoinColumn(name = "userId")
    private MUserEntity userEntity;
    private LocalDate orderDate;
    @ManyToOne
    @JoinColumn(name = "pickupTimeId")
    private MPickupTimeEntity pickupTimeEntity;
    @ManyToOne
    @JoinColumn(name = "pickupAddressId")
    private TPickupAddressEntity pickupAddressEntity;
    private Integer qty;
    private Integer status;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    @OneToOne(mappedBy = "pickupOrderRequestEntity")
    private TPickupDetailEntity pickupEntity;
    private String reason;
    @OneToMany(mappedBy = "orderRequestEntity", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TPickupOrderRequestDetailEntity> pickupOrderRequestDetails;

    public List<TPickupOrderRequestDetailEntity> getPickupOrderRequestDetails() {
        if (pickupOrderRequestDetails == null) {
            return new ArrayList<>();
        }

        return pickupOrderRequestDetails;
    }
}
