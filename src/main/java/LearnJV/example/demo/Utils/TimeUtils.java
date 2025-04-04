package LearnJV.example.demo.Utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {


    public static long getTimeToLiveEndOfDay(){
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();

        return Duration.between(now, midnight).getSeconds();
    }


}
