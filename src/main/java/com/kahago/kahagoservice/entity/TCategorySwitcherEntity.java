package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author bangd ON 20/11/2019
 * @project com.kahago.kahagoservice.entity
 */
@Entity
@Table(name = "t_category_switcher")
@Data
public class TCategorySwitcherEntity {
    @GeneratedValue
    @Id
    private Integer seqid;
    private Integer idUserCategory;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="switcherCode")
    private MSwitcherEntity switcherCode;
}
