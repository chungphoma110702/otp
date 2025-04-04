package LearnJV.example.demo.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ERROR_CODE {
    INVALID_FORMAT("ER100", "Invalid format"),
    FIELD_REQUIRED("ER101", "Field required"),
    PASSWORD_NOT_MATCH("ER102", "Password not match"),
    PASSWORD_NOT_STRONG("ER103", "Password is not strong enough"),
    PASSPORT_INVALID("ER104", "Passport invalid"),
    EMAIL_INVALID("ER105", "Email invalid"),
    EMAIL_HAS_BEEN_USED("ER106", "Email has been used"),
    PHONE_NUMBER_INVALID("ER107", "Phone number invalid"),
    VALUE_NOT_ALLOW_RANGE("ER108", "value is not within the allow range"),
    PASSCODE_NOT_MATCH("ER109", "Passcode not match"),
    PASSPORT_HAS_BEEN_USED("ER110", "Passport has been used"),
    INVALID_REQUEST("ER_400", "Invalid Request"),
    INVALID_PARAMETER("ER_401", "Invalid parameter"),
    RESOURCE_NOT_FOUND("ER_404", "Resource Not Found"),
    INTERNAL_SERVER_ERROR("ER_500", "Internal Server Error");

    private final String errorCode;
    private final String message;
}
