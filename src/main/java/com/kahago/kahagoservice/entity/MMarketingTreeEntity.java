package com.kahago.kahagoservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Entity
@Table(name="m_marketing_tree")
@Data
public class MMarketingTreeEntity {
	@Id
	@GeneratedValue
	private Integer id;
	private String userId;
	private String userIdParent;
}
