package LearnJV.example.demo.Controller;

import LearnJV.example.demo.Dto.Request.ConfirmOtpRegisterRequest;
import LearnJV.example.demo.Dto.Request.RegisterRequest;
import LearnJV.example.demo.Dto.Response.RegisterResponse;
import LearnJV.example.demo.Exception.ApplicationException;
import LearnJV.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) throws ApplicationException {
        return userService.registerUser(registerRequest);
    }

    @PutMapping("/resend-otp/{transactionId}")
    public RegisterResponse resendOtp(@PathVariable("transactionId") String transactionId) {
        return userService.resendOtp(transactionId);
    }

     //3. verify otp

    @PutMapping("/confirm-otp")
    public void confirmRegisterOtp(@RequestBody ConfirmOtpRegisterRequest request){
        userService.confirmRegisterOtp(request);
    }
}
