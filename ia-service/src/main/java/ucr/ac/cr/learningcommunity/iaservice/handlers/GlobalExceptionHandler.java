package ucr.ac.cr.learningcommunity.iaservice.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ucr.ac.cr.learningcommunity.iaservice.models.BaseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(String msg, int status) {}

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(
                        ex.getMessage() != null ? ex.getMessage() : ex.getErrorCode().getDefaultMessage(),
                        ex.getErrorCode().getHttpStatus()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse(
                        "INTERNAL SERVER ERROR",
                        500));
    }
}