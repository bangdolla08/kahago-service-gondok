package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "m_user_level")
@Data
public class MUserLevelEntity {
    @Id
    @GeneratedValue
    private Integer userLevel;
    private String levelName;
    private String lastUser;
    private Timestamp lastUpdate;
    private String accountType;
}
