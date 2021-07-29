package Exceptions;

public class InvalidAccountException extends Exception {
  public InvalidAccountException(int accountNum){
    super("Account number: " + accountNum + " does not exist");
  }
}