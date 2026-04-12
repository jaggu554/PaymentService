package com.payment.service.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.service.entity.Payment;

public interface PaymentRepository  extends JpaRepository<Payment,Long>{
	
	Optional<Payment> findByIdempotencyKey(String key);
	
	Optional<Payment> findByGateWayPaymentId(String gatewayPaymentId);
	
}
