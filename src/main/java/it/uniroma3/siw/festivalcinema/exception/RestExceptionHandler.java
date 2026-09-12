package it.uniroma3.siw.festivalcinema.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    //dati non validi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> fields.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(400, "Dati non validi", fields));
    }

    //x non ripetere review
    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateReview(DuplicateReviewException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(409, e.getMessage(), null));
    }

    //x modificare solo propria rec
    @ExceptionHandler(ReviewNotOwnedException.class)
    public ResponseEntity<Map<String, Object>> handleReviewNotOwned(ReviewNotOwnedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body(403, e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        logger.error("Errore non gestito nelle API", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(500, "Errore interno del server", null));
    }

    private Map<String, Object> body(int status, String message, Map<String, String> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("message", message);
        if (fields != null) {
            body.put("fields", fields);
        }
        return body;
    }
}
