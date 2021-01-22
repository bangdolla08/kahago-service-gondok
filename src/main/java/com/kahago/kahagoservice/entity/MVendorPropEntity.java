package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "m_vendor_prop")
@Data
public class MVendorPropEntity {
    @Id
    @GeneratedValue
    private Integer seq;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "switcherCode")
    private MSwitcherEntity switcherCode;
    private String action;
    private String url;
    private String clientCode;
    private String origin;
    private Integer pickupCount;
}
