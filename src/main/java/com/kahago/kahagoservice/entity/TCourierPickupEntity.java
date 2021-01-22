package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Hendro yuwono
 */
@Entity
@Table(name = "t_courier_pickup")
@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class TCourierPickupEntity {
    @Id
    @GeneratedValue
    @Column(name = "id_courier_pickup")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_id")
    private TPickupEntity pickup;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_addr_id")
    private TPickupAddressEntity pickupAddress;
    private String courierId;
    private LocalDateTime pickupActionTime;
    private LocalDateTime toCustomerTime;
    private LocalDateTime toWarehouseTime;
    private Integer status;
}
