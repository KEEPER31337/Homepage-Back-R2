package com.keeper.homepage.global.error;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> runtimeException() {
    return ResponseEntity.internalServerError()
        .body(ErrorResponse.from("서버에 문제가 생겼습니다."));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.from("권한이 없습니다."));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return typeMismatchErrorMessage(e.getName());
  }

  @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
  public ResponseEntity<ErrorResponse> methodArgumentConversionNotSupportedException(
      MethodArgumentConversionNotSupportedException e) {
    return typeMismatchErrorMessage(e.getName());
  }

  private static ResponseEntity<ErrorResponse> typeMismatchErrorMessage(String errorMessage) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from("%s 파라미터의 형식이 일치하지 않습니다.".formatted(errorMessage)));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> httpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(e.getMessage()));
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> bindException(BindException e) {
    String errorMessage = getErrorMessage(e);
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String errorMessage = getErrorMessage(e);
    return ResponseEntity.badRequest()
        .body(ErrorResponse.from(errorMessage));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> businessException(BusinessException e) {
    String errorMessage = getErrorMessage(e.getInvalidValue(), e.getFieldName(), e.getMessage());
    return ResponseEntity.status(e.getHttpStatus())
        .body(ErrorResponse.from(errorMessage));
  }

  private static String getErrorMessage(BindException e) {
    BindingResult bindingResult = e.getBindingResult();

    return bindingResult.getFieldErrors()
        .stream()
        .map(fieldError -> getErrorMessage((String) fieldError.getRejectedValue(),
            fieldError.getField(),
            fieldError.getDefaultMessage()))
        .collect(Collectors.joining(", "));
  }

  public static String getErrorMessage(String invalidValue, String errorField,
      String errorMessage) {
    return String.format("[%s] %s: %s", errorField, invalidValue, errorMessage);
  }
}
