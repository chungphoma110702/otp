package LearnJV.example.demo.Enum;

import java.util.stream.Stream;

public enum USER_STATUS {

    ACTIVE(1),
    INACTIVE(0),
    BLOCKED(-1);

    private final int value;

    USER_STATUS(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static USER_STATUS fromValue(int value) {
        return Stream.of(USER_STATUS.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
