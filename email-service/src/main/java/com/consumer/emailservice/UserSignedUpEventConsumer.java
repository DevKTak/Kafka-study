package com.consumer.emailservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSignedUpEventConsumer {

	private final EmailLogRepository emailLogRepository;

	@KafkaListener(
		topics = "user.signed-up",
		groupId = "email-service",
		concurrency = "3"
	)
	@RetryableTopic(
		// 현업에서 보통 재시도 횟수는 3~5회 사이로 정하는 편이다. 왜냐하면 재시도를 너무 많이 할 경우 시스템 부하가 커질 수 있고,
		// 너무 적으면 일시적인 장애에 대응하기 어렵기 때문이다.
		// 총 시도 횟수 (최초 시도 1회 + 재시도 4회)
		attempts = "5",

		// 첫 재시도 간격은 짧게 설정하는 편이고, 그 이후 재시도 간격은 지수적(exponential)으로 증가하도록 설정하는 편이다. 그래야 일시적인 장애에 대해서는 첫 빠른 재시도로 대응이 가능하고,
		// 장애가 조금 길어지는 경우라도 무의미하게 재시도하는 걸 방지하기 위함이다.
		// 재시도 간격 (1000ms -> 2000ms -> 4000ms -> 8000ms 순으로 재시도 시간이 증가한다.)
		backoff = @Backoff(delay = 1000, multiplier = 2),

		dltTopicSuffix = ".dlt"
	)
	public void consume(String message) throws InterruptedException {
		System.out.println("Kafka로부터 받아온 메시지: " + message);

		UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.fromJson(message);

		// 실제 이메일 발송 로직은 생략
		String receiverEmail = userSignedUpEvent.getEmail();
		String subject = userSignedUpEvent.getName() + "님, 회원 가입을 축하드립니다!";
		Thread.sleep(3000); // 이메일 발송에 3초 정도 시간이 걸리는 걸 가정
		System.out.println("이메일 발송 완료");

		//== 이메일 발송로그 (DB에 저장) ==//
		EmailLog emailLog = new EmailLog(
			userSignedUpEvent.getUserId(),
			receiverEmail,
			subject
		);
		emailLogRepository.save(emailLog);
	}
}
