package com.project.coupon.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name="pageinfo")
public class PageEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long pageId;
	private String pageName;
	private boolean status;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
}
