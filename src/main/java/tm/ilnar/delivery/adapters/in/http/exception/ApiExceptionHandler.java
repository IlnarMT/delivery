package tm.ilnar.delivery.adapters.in.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tm.ilnar.delivery.adapters.in.http.openapi.model.Error;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> handleBadRequest(
        BadRequestException e
    ) {
        return ResponseEntity
            .badRequest()
            .body(toHttpError(HttpStatus.BAD_REQUEST, e.getError().getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<tm.ilnar.delivery.adapters.in.http.openapi.model.Error> handleConflict(
        ConflictException e
    ) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(toHttpError(HttpStatus.CONFLICT, e.getError().getMessage()));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<tm.ilnar.delivery.adapters.in.http.openapi.model.Error> handleValidation(
        org.springframework.web.bind.MethodArgumentNotValidException ex
    ) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");

        return ResponseEntity.badRequest()
            .body(toHttpError(HttpStatus.BAD_REQUEST, msg));
    }

    private tm.ilnar.delivery.adapters.in.http.openapi.model.Error toHttpError(HttpStatus httpStatus, String message) {
        return new tm.ilnar.delivery.adapters.in.http.openapi.model.Error()
            .code(httpStatus.value())
            .message(message);
    }
}
