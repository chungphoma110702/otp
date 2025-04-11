package LearnJV.example.demo.Dto.Request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {

    private String phoneNumber;

    private String password;
}
