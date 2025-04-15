package LearnJV.example.demo.Utils;

import LearnJV.example.demo.Enum.ERROR_CODE;
import LearnJV.example.demo.Exception.ApplicationException;
import io.micrometer.common.util.StringUtils;

public class ValidateUtils {

    public static String validatePhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }

        phoneNumber = phoneNumber.replaceAll(" " , "");
        // check length :
        //+84982573860
        //0982573860
        //84982573860

        if (!phoneNumber.matches("^\\+?\\d+$")) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "phoneNumber contains invalid characters");
        }
        if (phoneNumber.length() < 10 || phoneNumber.length() > 12) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }
        // check prefix
        if (!phoneNumber.startsWith("0") && !phoneNumber.startsWith("+84") && !phoneNumber.startsWith("84")) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }

        if (phoneNumber.startsWith("0") && phoneNumber.length() == 11) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }

        if (phoneNumber.startsWith("0") && phoneNumber.length() == 12) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }

        if (phoneNumber.startsWith("84")) {
            return  phoneNumber;
        }

        if (phoneNumber.startsWith("0")) {
            return  "84" + phoneNumber.substring(1);
        }

        if (phoneNumber.startsWith("+84")) {
            return phoneNumber.substring(1);
        }

        return phoneNumber;

    }


    public static void validatePassword(String password) throws ApplicationException {

        if (org.apache.commons.lang3.StringUtils.isEmpty(password) || password.length() < 8) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "password is invalid");
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "password must contain at least one letter");
        }


    }

}
