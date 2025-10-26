package com.producer.userservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// MSA 구조 이벤트기반 아키텍처에서 많이 사용하는 네이밍 방식의 클래스명!!
// Kafka에 전송할 메시지 객체
public class UserSignedUpEvent {

	private Long userId;
	private String email;
	private String name;
}
