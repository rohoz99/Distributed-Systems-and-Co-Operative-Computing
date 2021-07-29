package Exceptions;

public class InvalidLoginException extends Exception {
  public InvalidLoginException() {
    super("Invalid Login Details");
  }
}