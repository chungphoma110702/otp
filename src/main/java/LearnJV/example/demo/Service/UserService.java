package LearnJV.example.demo.Service;

import LearnJV.example.demo.Dto.Request.ConfirmOtpRegisterRequest;
import LearnJV.example.demo.Dto.Request.RegisterRequest;
import LearnJV.example.demo.Dto.Response.RegisterResponse;
import LearnJV.example.demo.Entity.UserEntity;
import LearnJV.example.demo.Enum.ERROR_CODE;
import LearnJV.example.demo.Enum.USER_STATUS;
import LearnJV.example.demo.Exception.ApplicationException;
import LearnJV.example.demo.Redis.domains.OtpDomain;
import LearnJV.example.demo.Redis.entities.RegisterUserEntity;
import LearnJV.example.demo.Redis.repository.RegisterUserRedisRepository;
import LearnJV.example.demo.Repository.UserRepository;
import LearnJV.example.demo.Utils.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService
{

    private final OtpDomain otpDomain;

    private final UserRepository userRepository;

    private final RegisterUserRedisRepository registerUserRedisRepository;


    public UserService(OtpDomain otpDomain, UserRepository userRepository, RegisterUserRedisRepository registerUserRedisRepository) {
        this.otpDomain = otpDomain;
        this.userRepository = userRepository;
        this.registerUserRedisRepository = registerUserRedisRepository;
    }

    public RegisterResponse registerUser(RegisterRequest request) throws ApplicationException
    {

        //validate request
        this.validateUserRegisterRequest(request);

        // check user exist on db
        String phoneNumber = ValidateUtils.validatePhoneNumber(request.getPhoneNumber());
        log.info("[registerUser] - user register with phone {} START", phoneNumber);

        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber);

        if (userEntity != null)
        {
            log.info("[registerUser] request fail : user already exists with phone {}", phoneNumber);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "PhoneNumber is already exists");
        }
        // otp gen
        RegisterUserEntity otpEntity = otpDomain.genOtpWhenUserRegister(phoneNumber, request.getPassword());
        //log.debug("[registerUser] - user register with phone entity {} ", otpEntity);
        log.info("[registerUser] - user register with phone {} DONE", request.getPhoneNumber());
        log.info("[otp] - user register with otp {} DONE", otpEntity.getOtp());
        return new RegisterResponse(otpEntity);
    }

    protected void validateUserRegisterRequest(RegisterRequest request) throws ApplicationException
    {
        if (StringUtils.isBlank(request.getPhoneNumber())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "phoneNumber is invalid");
        }


        if (StringUtils.isBlank(request.getPassword())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "password is invalid");
        }

        ValidateUtils.validatePassword(request.getPassword());
    }


    public RegisterResponse resendOtp(String transactionId) throws ApplicationException
    {

        log.info("[resendOtp] - Start with transactionId {}", transactionId);

        // Lấy thông tin giao dịch từ Redis
        RegisterUserEntity registerUserEntity = registerUserRedisRepository.findById(transactionId)
                .orElseThrow(() -> new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Transaction ID not found"));

        long currentTime = System.currentTimeMillis() / 1000;

        // Kiểm tra xem có thể gửi lại OTP không (cần chờ thời gian resendOtpTime)
        if (currentTime < registerUserEntity.getOtpResendTime()) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Please wait before requesting OTP again");
        }

        // Kiểm tra số lần gửi lại OTP
        if (registerUserEntity.getOtpResendCount() >= 5) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP resend limit reached");
        }

        // Sinh OTP mới
        String newOtp = otpDomain.genOtp();
        registerUserEntity.setOtp(newOtp);
        registerUserEntity.setOtpResendTime(currentTime + 60);  // Đặt lại thời gian gửi lại OTP (1 phút sau)
        registerUserEntity.setOtpResendCount(registerUserEntity.getOtpResendCount() + 1); // Tăng số lần gửi lại

        // Cập nhật lại thông tin trong Redis
        registerUserRedisRepository.save(registerUserEntity);

        log.info("[resendOtp] - OTP resent successfully for transactionId {}", transactionId);
        log.info("[otp new] - OTP resent {}", registerUserEntity.getOtp());

        // Trả về kết quả response
        return new RegisterResponse(registerUserEntity);
    }


    public void confirmRegisterOtp(ConfirmOtpRegisterRequest request) throws ApplicationException {

        log.info("[confirmRegisterOtp] - Start with transactionId {}", request.getTransactionId());

        // Lấy thông tin giao dịch từ Redis bằng transactionId
        RegisterUserEntity registerUserEntity = registerUserRedisRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Transaction ID not found"));

        // Kiểm tra OTP đã hết hạn
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime > registerUserEntity.getOtpExpiredTime()) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP has expired");
        }

        // Kiểm tra OTP người dùng nhập có đúng không
        if (!registerUserEntity.getOtp().equals(request.getOtp())) {
            // Tăng số lần OTP sai
            registerUserEntity.setOtpFail(registerUserEntity.getOtpFail() + 1);
            registerUserRedisRepository.save(registerUserEntity);

            // Kiểm tra số lần sai OTP
            if (registerUserEntity.getOtpFail() >= 3) {
                throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP failed 3 times. Please try again later.");
            }

            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Invalid OTP");
        }

        // Nếu OTP đúng, bạn có thể cập nhật trạng thái người dùng và hoàn thành đăng ký
        UserEntity userEntity = new UserEntity();
        userEntity.setPhoneNumber(registerUserEntity.getPhoneNumber());
        userEntity.setPassword(registerUserEntity.getPassword());
        userEntity.setStatus(USER_STATUS.ACTIVE); // Cập nhật trạng thái người dùng thành ACTIVE

        // Lưu vào DB
        userRepository.save(userEntity);

        // Xóa thông tin OTP trong Redis sau khi xác nhận thành công
        registerUserRedisRepository.delete(registerUserEntity);

        log.info("[confirmRegisterOtp] - OTP confirmed successfully for transactionId {}", request.getTransactionId());
    }
}

