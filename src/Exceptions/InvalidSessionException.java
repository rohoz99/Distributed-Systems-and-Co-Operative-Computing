package Exceptions;

public class InvalidSessionException extends Exception {
  public InvalidSessionException() {
    super("Your session has timed out after 5 minutes of inactivity. Please login again with your username and password");
  }
}