package com.api.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local") // Força o uso do application-local.properties
class AuthServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
