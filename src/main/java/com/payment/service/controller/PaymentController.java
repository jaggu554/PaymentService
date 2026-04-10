package com.payment.service.controller;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.service.PaymentRequest;
import com.payment.service.PaymentService;
import com.payment.service.entity.Payment;

@RequestMapping("/payments")
@RestController 
public class PaymentController {

		@Autowired
		private PaymentService paymentService;
		
		@PostMapping
		public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request){
			Payment payment =paymentService.createPayment(request);
			return new ResponseEntity(payment,HttpStatus.CREATED);
		}
		
		@GetMapping("/force-race")
		public void forceRace() {
		    IntStream.range(0, 2).parallel().forEach(i -> {
		        PaymentRequest request = new PaymentRequest();
		        request.setOrderId("ORD123");
		        request.setAmount(1000.0);
		        request.setIdempotencyKey("ABC127");

		        paymentService.createPayment(request);
		    });
		}
}
