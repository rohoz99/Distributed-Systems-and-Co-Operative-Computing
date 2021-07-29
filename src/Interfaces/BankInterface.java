package Interfaces;


import Exceptions.InsufficientFundsException;
import Exceptions.InvalidLoginException;
import Exceptions.InvalidSessionException;
import Exceptions.StatementException;
import Server.Account;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface BankInterface extends Remote {

  public long login(String username, String password) throws RemoteException, InvalidLoginException;


  public double withdraw(int accountNum, double amount, long sessionID) throws RemoteException, InvalidSessionException,InsufficientFundsException;

  public double deposit(int accountNum, double amount, long sessionID) throws RemoteException, InvalidSessionException;

  public Account getAccountDetails(long sessionID) throws RemoteException, InvalidSessionException;


  public StatementInterface getStatement(int accountNum, Date from, Date to, long sessionID) throws RemoteException, InvalidSessionException, InsufficientFundsException, StatementException;
  public Account getBalance(int accountNum, long sessionID) throws RemoteException, InvalidSessionException;


}