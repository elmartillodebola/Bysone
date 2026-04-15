package com.bysone.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requiere infraestructura real (DB) — ejecutar solo en integración")
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
