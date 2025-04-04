package LearnJV.example.demo.Entity.Converters;

import LearnJV.example.demo.Enum.USER_STATUS;
import jakarta.persistence.AttributeConverter;

public class UserStatusConverter implements AttributeConverter<USER_STATUS, Integer> {


    @Override
    public Integer convertToDatabaseColumn(USER_STATUS userStatus) {
        return userStatus.getValue();
    }

    @Override
    public USER_STATUS convertToEntityAttribute(Integer integer) {
        return USER_STATUS.fromValue(integer);
    }
}
