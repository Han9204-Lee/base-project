package com.example.security;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ClientSecretGenerator {
	private static final int SECRET_BYTE_SIZE = 32;
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	/**
	 * 시스템 인증용 client_id 와 client_secret 을 생성하고 secret 은 단방향 암호화하여 저장하도록 구성
	 */
	public static void main(String[] args) {
		String clientId = UUID.randomUUID().toString();
		String rawSecret = generateRawSecret();
		String hashedSecret = encoder.encode(rawSecret);

		System.out.println("=== Client 인증 정보 ===");
		System.out.println("client_id: " + clientId);
		System.out.println("client_secret (원본): " + rawSecret);
		System.out.println("client_secret (저장용): " + hashedSecret);
	}

	private static String generateRawSecret() {
		byte[] bytes = new byte[SECRET_BYTE_SIZE];
		new SecureRandom().nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}