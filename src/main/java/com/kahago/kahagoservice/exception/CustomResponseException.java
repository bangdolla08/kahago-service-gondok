package com.kahago.kahagoservice.exception;

import com.google.common.base.CaseFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@ControllerAdvice
public class CustomResponseException extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMapper errorMap = new ErrorMapper();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMap.addErrors(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getField()), error.getDefaultMessage());
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errorMap.addErrors(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getObjectName()), error.getDefaultMessage());
        }

        CustomError apiError = CustomError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("invalid validation")
                .errors(errorMap.getErrors())
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    /**
    *  This arguments showing when invalid parameter in controller
    * */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, ex.getParameterName()) + " parameter is missing";

        CustomError apiError = CustomError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getLocalizedMessage())
                .error(Collections.singletonList(error))
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> details = ex.getConstraintViolations()
                .parallelStream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        CustomError apiError = CustomError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("invalid validation")
                .error(details)
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(Exception ex, WebRequest request) {
        CustomError apiError = CustomError.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
//                .error(Collections.singletonList(ex.getMessage()))
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(Exception ex, WebRequest request) {
        CustomError apiError = CustomError.builder()
                .code(HttpStatus.CONFLICT.value())
                .status(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        CustomError apiError = CustomError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("invalid validation")
                .error(Collections.singletonList(error))
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        CustomError apiError = CustomError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("invalid validation")
                .error(Collections.singletonList(ex.getCause().getMessage()))
                .path(((ServletWebRequest) request).getRequest().getRequestURL().toString())
                .build();

        return new ResponseEntity<>(apiError, headers, apiError.getStatus());
    }
}
