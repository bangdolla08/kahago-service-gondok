package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_tutorial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MTutorialEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private Integer step;
    private String pathImage;
    private String pathImageBack;
    private Integer jenisTutorial;
    private String lastUser;
    private LocalDateTime lastUpdate;
    private String description;
    private String pathBlastImage;
    private Integer showDashboard;
    private String promoName;
    // Pak Yuli
}
