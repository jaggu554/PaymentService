package com.payment.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.service.constant.paymentStatus;
import com.payment.service.entity.Payment;
import com.payment.service.repo.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository repository;
	
	
	public Payment createPayment(PaymentRequest request) {
		
		// idempotency check
		Optional<Payment> existing= repository.findByIdempotencyKey(request.getIdempotencyKey());
		
		if(existing.isPresent()) {
			return existing.get();
		}
		
		
		try {
			Thread.sleep(10000); //10 seconds pause
			
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		// create payment
		Payment payment =new Payment();
		
		payment.setOrderId(request.getOrderId());
		payment.setAmount(request.getAmount());
		
		payment.setIdempotencyKey(request.getIdempotencyKey());
		payment.setCreatedAt(LocalDateTime.now());
		
		try {
			payment=repository.save(payment);
		}catch(Exception e) {
			System.out.println("Exception Handled");
		}
		
		
		
		// simulate gateway call
		boolean success=callGateWay();
		
		if(success) {
			payment.setStatus(paymentStatus.SUCCESS);
			payment.setGateWayPaymetId(UUID.randomUUID().toString());
			
		}else {
			payment.setStatus(paymentStatus.FAILED);
		}
		

		payment.setUpdateAt(LocalDateTime.now());

		return repository.save(payment);
	}


	private boolean callGateWay() {
		
		return Math.random() > 0.2; // simulate success/failure
	}
	
	
}
