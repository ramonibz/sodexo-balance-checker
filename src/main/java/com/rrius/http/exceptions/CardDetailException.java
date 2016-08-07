package com.rrius.http.exceptions;

public class CardDetailException extends RuntimeException {

  public CardDetailException() {
    super();
  }

  public CardDetailException(String message) {
    super(message);
  }

  public CardDetailException(String message, Throwable cause) {
    super(message, cause);
  }

  public CardDetailException(Throwable cause) {
    super(cause);
  }
}
