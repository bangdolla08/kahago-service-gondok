package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_session_switch")
@Data
public class MSessionSwitchEntity {
    @Id
    private Integer seqid;
    private String username;
    private String password;
}
