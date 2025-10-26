package com.producer.userservice;

import lombok.Getter;

@Getter
public class SignUpRequestDto {

	private String email;
	private String name;
	private String password;
}
