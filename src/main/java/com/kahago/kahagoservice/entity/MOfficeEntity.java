package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "m_office")
@Data
public class MOfficeEntity {
    @Id
    private String officeCode;
    private String parentOffice;
    private String name;
    private String unitType;
    private String address;
    private String city;
    private String postalCode;
    private String telp;
    private String fax;
    private String statusLayanan;
    private String lastUser;
    private Timestamp lastUpdate;
    private String regionCode;
    private String caCode;
    private String counter;
    private String caCodeDst;
    private String branchDst;
    private Byte isActive;
    private String longitude;
    private String latitude;
    @ManyToOne
    @JoinColumn(name="pickupAddrId")
    private TPickupAddressEntity pickupAddrId;
    @OneToMany(mappedBy = "officeCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TOfficeEntity> office;
    private Integer areaKotaId;
}
