package LearnJV.example.demo.Redis.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("otp")
@Data
public class OtpLimitEntity {
    @Id
    private String phoneNumber;

    private int dailyOtpCounter = 0;
    @TimeToLive
    private long ttl;
}