package com.pdfdesk.constants;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
  USER_NOT_FOUND(101, "User not found. Please sign up first."),
  IS_GOOGLE_PROVIDER(102, "This email uses Google sign-in. Please sign in with Google."),
  IS_GMAIL_PASS_PROVIDER(103, "This email uses Email/Password sign-in. Please sign in with email and password."),
  INVALID_PASSWORD(104, "Invalid password."),
  USED_EMAIL(105, "Email already registered. Please sign in or use another email.");

  private final int code;
  private final String message;

  AuthErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}