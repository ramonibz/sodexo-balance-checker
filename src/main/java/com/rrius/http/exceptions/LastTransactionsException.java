package com.rrius.http.exceptions;


public class LastTransactionsException extends RuntimeException{
  public LastTransactionsException() {
    super();
  }

  public LastTransactionsException(String message) {
    super(message);
  }

  public LastTransactionsException(String message, Throwable cause) {
    super(message, cause);
  }

  public LastTransactionsException(Throwable cause) {
    super(cause);
  }
}
