package com.example.products.exceptions;

public class TokenExpiredException extends AuthenticationException {

  public TokenExpiredException(String message) {
    super(message);
  }

  public TokenExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}