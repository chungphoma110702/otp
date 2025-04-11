package LearnJV.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/test.properties")
class OtpApplicationTests {

	@Test
	void contextLoads() {
	}

}
