package com.example.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_status_id")
    private PaymentStatus paymentStatus;

    @Column(length = 100)
    private String method;

    private Integer amount;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

	    public Payment(
	        PaymentStatus paymentStatus,
	        String method,
	        Integer amount,
	        String transactionId
	) {
	    this.paymentStatus = paymentStatus;
	    this.method = method;
	    this.amount = amount;
	    this.transactionId = transactionId;
	}
}