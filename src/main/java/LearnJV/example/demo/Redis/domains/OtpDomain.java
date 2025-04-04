package LearnJV.example.demo.Redis.domains;

import LearnJV.example.demo.Enum.ERROR_CODE;
import LearnJV.example.demo.Exception.ApplicationException;
import LearnJV.example.demo.Redis.entities.OtpLimitEntity;
import LearnJV.example.demo.Redis.entities.RegisterUserEntity;
import LearnJV.example.demo.Redis.repository.OtpLimitRedisRepository;
import LearnJV.example.demo.Redis.repository.RegisterUserRedisRepository;
import LearnJV.example.demo.Utils.BcryptUtils;
import LearnJV.example.demo.Utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OtpDomain {

    private static final SecureRandom random = new SecureRandom();
    private static final DecimalFormat formatter =new DecimalFormat("000000");

    @Autowired
    private OtpLimitRedisRepository otpLimitRedisRepository;

    @Autowired
    private RegisterUserRedisRepository registerUserRedisRepository;


    public OtpLimitEntity validateLimitOtpByPhoneNumber(String phoneNumber){

        log.info("[validateLimitOtpByPhoneNumber] START with phone {}", phoneNumber);

        // check otp limit
        Optional<OtpLimitEntity> otpLimit = otpLimitRedisRepository.findById(phoneNumber);

        if (otpLimit.isEmpty()){

            OtpLimitEntity entity = new OtpLimitEntity();
            entity.setPhoneNumber(phoneNumber);
            entity.setDailyOtpCounter(0);


            long timeTolive = TimeUtils.getTimeToLiveEndOfDay();
            entity.setTtl(timeTolive);
            return entity;
        }
        OtpLimitEntity otpLimitEntity =otpLimit.get();


        if (otpLimitEntity.getDailyOtpCounter() >= 5){
            log.info("[validateLimitOtpByPhoneNumber] request fail : otp limit reached with phone {}", phoneNumber);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP limit reached");
        }
        log.info("[validateLimitOtpByPhoneNumber] DONE with phone {}", phoneNumber);

        return otpLimitEntity;

    }


    public RegisterUserEntity genOtpWhenUserRegister(String phoneNumber, String password){
        log.info("[genOtpWhenUserRegister] START with phone {}", phoneNumber);

        // validate otp limit daily
        OtpLimitEntity otpLimit = this.validateLimitOtpByPhoneNumber(phoneNumber);

        String otp = genOtp();

        // save otp to redis
        RegisterUserEntity registerUserEntity = new RegisterUserEntity();

        String transactionId = UUID.randomUUID().toString();
        registerUserEntity.setTransactionId(transactionId);
        log.info("[genOtpWhenUserRegister] transactionId {} with phoneNumber {}", transactionId, phoneNumber);
        registerUserEntity.setOtp(otp);
        registerUserEntity.setPhoneNumber(phoneNumber);
        registerUserEntity.setPassword(BcryptUtils.encode(password));
        registerUserEntity.setOtpFail(0);
        registerUserEntity.setOtpExpiredTime(System.currentTimeMillis()/ 1000 + 300);
        registerUserEntity.setOtpResendTime(System.currentTimeMillis()/ 1000 + 60);

        registerUserEntity.setTtl(900);

        registerUserRedisRepository.save(registerUserEntity);

        otpLimit.setDailyOtpCounter(otpLimit.getDailyOtpCounter() + 1);
        otpLimitRedisRepository.save(otpLimit);

        log.info("[genOtpWhenUserRegister] DONE with phone {}", phoneNumber);
        return registerUserEntity;
    }

    public String genOtp(){
        int otp = random.nextInt(1000000);
        return formatter.format(otp);
    }

}
