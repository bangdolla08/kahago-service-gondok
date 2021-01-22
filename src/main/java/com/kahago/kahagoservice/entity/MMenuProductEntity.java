package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_menu_product")
@Data
public class MMenuProductEntity {
    @Id
    @GeneratedValue
    private Integer menuProductCode;
    private String productSwCode;
    private String name;
    private String displayName;
    private Integer parentMenu;
    private String level;
    private String link;
    private String lastUser;
    private Timestamp lastUpdate;
    private String kategori;
}
