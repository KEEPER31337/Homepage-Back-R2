package com.keeper.homepage.global.config.security.session;

public class SessionStoreException extends RuntimeException {

  public SessionStoreException(String message) {
    super(message);
  }

  public SessionStoreException(String message, Throwable cause) {
    super(message, cause);
  }
}
