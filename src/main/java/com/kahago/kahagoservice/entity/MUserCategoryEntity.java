package com.kahago.kahagoservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "m_user_category")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MUserCategoryEntity {
    @Id
    @GeneratedValue
    private Integer seqid;
    private String nameCategory;
    private Integer accountType;
    private LocalDateTime lastUpdate;
    private String lastUser;
    @Column(name="ROLE_NAME")
    private String roleName;
//    private Boolean isWarehouse;
}
