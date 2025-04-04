package LearnJV.example.demo.Dto.Request;


import lombok.Data;

@Data
public class ConfirmOtpRegisterRequest {
    private String transactionId;
    private String otp;

}
