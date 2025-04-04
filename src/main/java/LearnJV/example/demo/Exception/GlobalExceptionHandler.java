package LearnJV.example.demo.Exception;

import LearnJV.example.demo.Dto.Response.BadRequestResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    protected final HttpServletRequest httpServletRequest;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("ERROR: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(LearnJV.example.demo.Exception.ApplicationException.class)
    public ResponseEntity<BadRequestResponse> handleApplicationException(ApplicationException ex) {
        log.info("handleApplicationException {} with message {} , title {} , data {} ", ex.getCode(), ex.getMessage(), ex.getTitle(), ex.getData());

        BadRequestResponse responseData = new BadRequestResponse(ex, httpServletRequest);
        String traceId = UUID.randomUUID().toString();

        responseData.setRequestId(traceId);

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }
}
