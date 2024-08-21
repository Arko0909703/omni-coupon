package com.project.coupon.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.coupon.entity.BaseCoupon;

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
@Table(name="userinfo")
public class UserEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String firstName;
	private String lastName;
	private boolean status;
	private String phone;
	private String email;
	private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private Integer roleId;
    private boolean emailNotification;
    private boolean smsNotification;
    private String password;
}
