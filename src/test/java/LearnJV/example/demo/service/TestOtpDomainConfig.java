package LearnJV.example.demo.service;

import LearnJV.example.demo.Redis.domains.OtpDomain;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestOtpDomainConfig {

    @Bean
    public OtpDomain otpDomain() {
        return Mockito.mock(OtpDomain.class);
    }
}