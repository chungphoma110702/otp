package LearnJV.example.demo.service;


import LearnJV.example.demo.Dto.Request.ConfirmOtpRegisterRequest;
import LearnJV.example.demo.Dto.Request.RegisterRequest;
import LearnJV.example.demo.Dto.Response.RegisterResponse;
import LearnJV.example.demo.Entity.UserEntity;
import LearnJV.example.demo.Exception.ApplicationException;
import LearnJV.example.demo.Redis.domains.OtpDomain;
import LearnJV.example.demo.Redis.entities.RegisterUserEntity;
import LearnJV.example.demo.Redis.repository.RegisterUserRedisRepository;
import LearnJV.example.demo.Repository.UserRepository;
import LearnJV.example.demo.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private OtpDomain otpDomain;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RegisterUserRedisRepository registerUserRedisRepository;


    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;
    private RegisterUserEntity registerUserEntity;


    @BeforeEach
    void initData(){

        registerRequest = RegisterRequest.builder()
                .phoneNumber("840332967512")
                .password("Aa@123456")
                .build();

        registerUserEntity = RegisterUserEntity.builder()
                .phoneNumber("840332967512")
                .password("Aa@123456")
                .transactionId("abc")
                .otp("123456")
                .otpResendCount(0)
                .otpResendTime(0)
                .otpExpiredTime(System.currentTimeMillis() / 1000 + 120)
                .otpFail(0)
                .build();

    }

    @Test
    void Register_success(){

        when(userRepository.findByPhoneNumber(anyString())).thenReturn(null);
        when(otpDomain.genOtpWhenUserRegister("840332967512", "Aa@123456"))
                .thenReturn(registerUserEntity);


        RegisterResponse response = userService.registerUser(registerRequest);

        assertThat(response.getTransactionId()).isEqualTo("abc");
    }

    @Test
    void registerUser_UserExisted() {
        UserEntity existing = new UserEntity();
        existing.setPhoneNumber("840332967512");

        when(userRepository.findByPhoneNumber(anyString())).thenReturn(existing);

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                userService.registerUser(registerRequest));

        assertThat(ex.getMessage()).isEqualTo("PhoneNumber is already exists");
        assertThat(ex.getCode()).isEqualTo("ER_400");
    }

    @Test
    void ResendOtp_Success(){

        String transactionId = "abc";
        long now = System.currentTimeMillis() / 1000;

        registerUserEntity.setOtpResendCount(1);
        registerUserEntity.setOtpResendTime(now - 10);

        when(registerUserRedisRepository.findById(transactionId)).thenReturn(Optional.of(registerUserEntity));
        when(otpDomain.genOtp()).thenReturn("654321");

        var response = userService.resendOtp(transactionId);

        assertThat(response.getTransactionId()).isEqualTo("abc");
        assertThat(registerUserEntity.getOtpResendCount()).isEqualTo(2);

    }

    @Test
    void ConfirmOtp_success()  {
        ConfirmOtpRegisterRequest request = new ConfirmOtpRegisterRequest();
        request.setTransactionId("abc");
        request.setOtp("123456");

        when(registerUserRedisRepository.findById("abc")).thenReturn(Optional.of(registerUserEntity));

        userService.confirmRegisterOtp(request);
    }

}
