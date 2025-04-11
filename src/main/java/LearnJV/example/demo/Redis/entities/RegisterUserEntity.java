package LearnJV.example.demo.Redis.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("register_user")
public class RegisterUserEntity {

    @Id
    private String transactionId;

    private String otp;

    private long otpExpiredTime;

    private long otpResendTime;

    private int otpResendCount;

    private String phoneNumber;

    private String password;

    private int otpFail;

    @TimeToLive
    private long ttl;

}