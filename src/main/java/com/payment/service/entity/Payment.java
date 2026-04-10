package com.payment.service.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import com.payment.service.constant.paymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
	
	
	@GeneratedValue
	@Id
	private Long id;
	
	private String orderId;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private paymentStatus status;
	
	private String idempotencyKey;
	
	private String gateWayPaymetId;
	
	@Column(name="created_at",insertable=true,updatable=false)
	@CreatedDate
	private LocalDateTime createdAt;
	
	@Column(name="upated_at",insertable=false, updatable=true)
	@UpdateTimestamp
	private LocalDateTime updateAt;
	
}
