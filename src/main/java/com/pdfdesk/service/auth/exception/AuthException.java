package com.pdfdesk.service.auth.exception;

import com.pdfdesk.constants.AuthErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
  private final int code;

  public AuthException(AuthErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

}