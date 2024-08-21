package com.project.coupon.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name="terms_conditions")
@Data
public class TermsAndConditionEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(name="name", length=30,unique=true)
	private String name;
	@Column(name="description")
	private String description;
	@Column(name="create_date")
	private LocalDateTime createDate;
	@Column(name="modified_date")
	private LocalDateTime modifiedDate;
	
}
