package com.consumer.emailservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_logs")
public class EmailLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long receiverUserId;
	private String receiverEmail;
	private String subject;
	public EmailLog() {
	}
	public EmailLog(Long receiverUserId, String receiverEmail, String subject) {
		this.receiverUserId = receiverUserId;
		this.receiverEmail = receiverEmail;
		this.subject = subject;
	}
}
