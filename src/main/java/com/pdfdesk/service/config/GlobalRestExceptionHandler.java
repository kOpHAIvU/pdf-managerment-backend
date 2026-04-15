package com.pdfdesk.service.config;

import com.pdfdesk.service.common.ForbiddenException;
import com.pdfdesk.service.common.NotFoundException;
import com.pdfdesk.service.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(this::formatFieldError)
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(ApiResponse.failure(message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ApiResponse<Void>> handleFileTooLarge(MaxUploadSizeExceededException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.failure("Uploaded file exceeds maximum allowed size"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
    log.error("Unhandled API exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.failure("Internal server error"));
  }

  private String formatFieldError(FieldError fieldError) {
    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
  }
}
