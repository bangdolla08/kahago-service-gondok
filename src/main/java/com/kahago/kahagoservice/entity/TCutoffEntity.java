package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_cutoff")
@Data
public class TCutoffEntity {
    @Id
    private Integer cuttOffId;
    private Integer time;
    private Integer productSwCode;
}
