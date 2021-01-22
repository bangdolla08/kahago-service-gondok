package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;


import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "m_switcher")
@Data
public class MSwitcherEntity {
    @Id
    @GeneratedValue
    private Integer switcherCode;
    private String name;
    private String displayName;
    private String address;
    private String pic;
    private String picTelp;
    private String startWeight;
    private String endWeight;
    private String lastUser;
    private Timestamp lastUpdate;
    private String img;
    private Boolean showall;
    @OneToMany
    @JoinColumn(name = "switcherCode")
    private List<TCategorySwitcherEntity> tCategorySwitcherEntities;
//    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TAreaEntity> transAreaDetail;

    @OneToMany(mappedBy = "switcherCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MVendorPropEntity> vendorProperties;

}
