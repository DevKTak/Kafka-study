package com.producer.userservice;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;

	public void signUp(SignUpRequestDto signUpRequestDto) {

		//== 회원 가입한 사용자 정보 DB에 저장 ==//
		User user = User.builder()
			.email(signUpRequestDto.getEmail())
			.name(signUpRequestDto.getName())
			.password(signUpRequestDto.getPassword())
			.build();
		User savedUser = userRepository.save(user);

		//== 카프카에 메시지 전송 ==//
		UserSignedUpEvent userSignedUpEvent = new UserSignedUpEvent(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getName()
		);
		this.kafkaTemplate.send("user.signed-up", toJsonString(userSignedUpEvent));
	}

	// Json 형태의 String 으로 만들기
	private String toJsonString(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(" Json 직렬화 실패");
		}
	}
}
