package com.example.ecommerce.errors;


import com.example.ecommerce.exception.InvalidUserIdException;
import com.example.ecommerce.exception.PasswordValidationException;
import com.example.ecommerce.exception.UsernameExistsException;
import com.example.ecommerce.exception.UsernameNotFoundException;
import com.example.ecommerce.util.LogMF;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the Error controller related to any errors handled by the Vehicles API
 */
@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_VALIDATION_FAILED_MESSAGE = "Validation failed.";

    @ExceptionHandler(PasswordValidationException.class)
    public final ResponseEntity<Object> handlePasswordValidationException(PasswordValidationException ex, WebRequest request) {
        logger.error(LogMF.format("handlePasswordValidationException", ex.getLocalizedMessage(), ex));
        return handleExceptionMessageOnly(ex, request);
    }

    @ExceptionHandler(UsernameExistsException.class)
    public final ResponseEntity<Object> handleUsernameExistsException(UsernameExistsException ex, WebRequest request) {
        logger.error(LogMF.format("handleUsernameExistsException", ex.getLocalizedMessage(), ex));
        return handleExceptionMessageOnly(ex, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        logger.error(LogMF.format("handleUsernameNotFoundException", ex.getLocalizedMessage(), ex));
        return handleExceptionMessageOnly(ex, request);
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public final ResponseEntity<Object> handleInvalidUserIdException(InvalidUserIdException ex, WebRequest request) {
        logger.error(LogMF.format("handleInvalidUserIdException", ex.getLocalizedMessage(), ex));
        return handleExceptionMessageOnly(ex, request);
    }

    private final ResponseEntity<Object> handleExceptionMessageOnly(Exception ex, WebRequest request) {
        ApiError error = new ApiError(ex.getLocalizedMessage(), null);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(
                        Collectors.toList());

        ApiError apiError = new ApiError(DEFAULT_VALIDATION_FAILED_MESSAGE, errors);
        logger.error(LogMF.format("handleMethodArgumentNotValid",
                apiError.getErrors()
                        .stream()
                        .map((message) -> {return String.valueOf(message);})
                        .collect(Collectors.joining(","))));
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }
}
