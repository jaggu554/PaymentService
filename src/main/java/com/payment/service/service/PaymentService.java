package com.payment.service.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.payment.service.constant.paymentStatus;
import com.payment.service.dto.PaymentRequest;
import com.payment.service.entity.Payment;
import com.payment.service.repo.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository repository;
	
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	
	public Payment createPayment(PaymentRequest request) {
		
		// sentex locking only request need to save in DB. if multiple requests comes
		String lockKey="lock:"+request.getIdempotencyKey();
		
		Boolean isLocked=redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED",10,TimeUnit.MINUTES);
		
		if(!Boolean.TRUE.equals(isLocked)) {
			throw new RuntimeException("Duplicate request in progress");
		}
		
		String key="payment:"+request.getIdempotencyKey();
		
		// Redis Check 
		String existingPayment=redisTemplate.opsForValue().get(key);
		
		if(existingPayment!=null) {
			return repository.findById(Long.valueOf(existingPayment)).orElse(null);
		}
		
		
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
//		boolean success=callGateWay();
//		
//		if(success) {
//			payment.setStatus(paymentStatus.SUCCESS);
//			payment.setGateWayPaymetId(UUID.randomUUID().toString());
//			
//		}else {
//			payment.setStatus(paymentStatus.FAILED);
//		}
		
		// passing information into the webhook to decide the payment is failed or passed.
		String paymentGatewayId=UUID.randomUUID().toString();
		payment.setGateWayPaymentId(paymentGatewayId);
		payment.setStatus(paymentStatus.PENDING);
		
		payment.setUpdateAt(LocalDateTime.now());
		
		redisTemplate.opsForValue().set(key, String.valueOf(payment.getId()));

		return repository.save(payment);
	}


	private boolean callGateWay() {
		
		return Math.random() > 0.2; // simulate success/failure
	}
	
	
}
