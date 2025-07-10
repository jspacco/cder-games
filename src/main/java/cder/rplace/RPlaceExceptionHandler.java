package cder.rplace;

import static java.lang.String.format;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RPlaceExceptionHandler
{

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex)
    {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("code", 401);
        body.put("timestamp", Instant.now());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(BadPixelRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadPixel(BadPixelRequestException ex)
    {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("code", 400);
        body.put("timestamp", Instant.now());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) 
    {
        String body = String.format(
            "Error!\n\nBad Request.\n\n"+
            "Missing required parameter: %s", ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "text/plain; charset=UTF-8")
            .body(body);
    }
}
