package com.payment.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

	private String orderId;
	
	private Double amount;
	
	private String idempotencyKey;
}
