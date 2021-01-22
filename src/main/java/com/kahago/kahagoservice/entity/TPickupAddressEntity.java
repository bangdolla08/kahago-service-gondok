package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_pickup_address")
@Data
public class TPickupAddressEntity {
    @Id
    @GeneratedValue
    private Integer pickupAddrId;
    private String address;
    @ManyToOne
    @JoinColumn(name = "idPostalCode" )
    private MPostalCodeEntity postalCode;
    @ManyToOne
    @JoinColumn(name = "userId")
    private MUserEntity userId;
    private String description;
    private String longitude;
    private String latitude;
    private Integer statusAlive;
    private Integer flag;
}
