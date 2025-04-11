package LearnJV.example.demo.Dto.Response;

import LearnJV.example.demo.Redis.entities.RegisterUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private String transactionId;

    private long otpExpiredTime;

    private long resendOtpTime;

    public RegisterResponse(RegisterUserEntity entity){
        this.transactionId = entity.getTransactionId();
        this.otpExpiredTime = entity.getOtpExpiredTime() - System.currentTimeMillis()/1000;
        this.resendOtpTime = entity.getOtpResendTime() - System.currentTimeMillis()/1000;
    }
}
