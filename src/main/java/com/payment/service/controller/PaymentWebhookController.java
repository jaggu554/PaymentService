package com.payment.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.service.constant.paymentStatus;
import com.payment.service.entity.Payment;
import com.payment.service.repo.PaymentRepository;

@RestController
@RequestMapping("/webhook")
public class PaymentWebhookController {

	@Autowired
	private PaymentRepository repo;

	@PostMapping("/payments")
	public ResponseEntity<String> handleResponse(@RequestBody Map<String, String> payload) {

		String gatewayPayment = payload.get("gatewayPaymentId");
		String status = payload.get("status");

		Payment payment = repo.findByGateWayPaymentId(gatewayPayment)
				.orElseThrow(() -> new RuntimeException("payment not found"));

		if (payment.getStatus() == paymentStatus.SUCCESS) {
			return new ResponseEntity<>("Already Processed...", HttpStatus.CREATED);

		}
		
		if("SUCCESS".equals(status)) {
			payment.setStatus(paymentStatus.SUCCESS);
		}else {
			payment.setStatus(paymentStatus.FAILED);
		}
		
		repo.save(payment);
		return new ResponseEntity<>("Webhook Processed...",HttpStatus.OK);
	}
}
