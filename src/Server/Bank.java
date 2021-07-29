package Server;
import Interfaces.BankInterface;
import Exceptions.*;

import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Bank extends UnicastRemoteObject implements BankInterface {

  private List<Session> sessions,finishedSessions; //sessions

  private List<Account> accounts; // users accounts

  public Bank() throws RemoteException
  {
    super();

    sessions = new ArrayList<>();
    finishedSessions = new ArrayList<>();
    accounts = new ArrayList<>();

    accounts.add(new Account("rohoz","jugz"));
    accounts.add(new Account("kingdra","ash") );
    accounts.add(new Account("coolio","roraj99") );

  }

  public static void main(String args[]) throws Exception {
    try {
// setting up security manager
      //System.setProperty("java.security.policy","file:C:\\Users\\rohin\\OneDrive\\Documents\\Java RMI Assignment 1\\src\\sec.policy");
    //  System.setSecurityManager(new SecurityManager());
     // System.out.println("\n Security Manager Set.......");

      String name = "BankApp";
      BankInterface bank = new Bank();

      Registry registry = LocateRegistry.createRegistry(8080);
      registry.rebind(name, bank);
      System.out.println("Server Started\n........................\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public long login(String username, String password) throws RemoteException, InvalidLoginException {
    //Loops through accs in order to find matching account with credentials
    for(Account acc : accounts) {
      if(username.equals(acc.getUserName()) && password.equals(acc.getPassword())){
        System.out.println(">> Account " + acc.getAccountNumber() + " logged in");
        //Create a new session when logged in and and return ID to the client
        Session s = new Session(acc); // new session ID
        sessions.add(s);
        return s.sessionId;
      }
    }
    throw new InvalidLoginException();
  }


  @Override
  public double withdraw(int accountnum, double amount, long sessionID) throws RemoteException,
      InsufficientFundsException, InvalidSessionException {
    //Checking if user is active
    if(checkSessionActive(sessionID)) {
      try {
        Account account = getAccount(accountnum);  // finding correct user
        if (account.getBalance() > 0 && account.getBalance() - amount >= 0) {
          account.setBalance(account.getBalance() - amount);

          //create new Transaction and add to account
          Transaction t = new Transaction(account, "Withdrawal");
          t.setAmount(amount);
          account.addTransaction(t);

          System.out.println("Euro:" + amount + " withdrawn from account: " + accountnum + "\n");

          //ouput new balance
          return account.getBalance();
        }
      } catch (InvalidAccountException e) {
        e.printStackTrace();
      }
    }
    throw new InsufficientFundsException();
  }

  @Override
  public double deposit(int accountnum, double amount, long sessionID) throws RemoteException, InvalidSessionException {
    if(checkSessionActive(sessionID)) {
      Account account;
      try {
        //finding the right account
        account = getAccount(accountnum);
        account.setBalance(account.getBalance() + amount);
        Transaction t = new Transaction(account, "Deposit"); // Creating a transaction and adding it
        t.setAmount(amount); // setting the ammount
        account.addTransaction(t); //adding to the list

        System.out.println("Euro" + amount + " deposited to the account: " + accountnum + "\n");

        return account.getBalance();
      } catch (InvalidAccountException e) {
        e.printStackTrace();
      }
    }
    return 0;
  }

  @Override
  public Account getBalance(int accountNum, long sessionID) throws RemoteException, InvalidSessionException {
    if(checkSessionActive(sessionID)) {
      try {
        Account account = getAccount(accountNum);
        System.out.println("Balance for account: " + accountNum + "\n");
        return account;
      } catch (InvalidAccountException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  public Statement getStatement(int accountNum, Date from, Date to, long sessionID) throws RemoteException,
      InvalidSessionException, StatementException {
    if(checkSessionActive(sessionID)) {
      try {
        Account account = getAccount(accountNum); // find right user

        System.out.println("Statement for account: " + accountNum +
            " between " + from.toString() + " " + to.toString() + "\n");
        Statement s = new Statement(account, from, to);
        return s;
      } catch (InvalidAccountException e) {
        e.printStackTrace();
      }
    }
    throw new StatementException("Statement cannot be gathered");
  }

  @Override
  public Account getAccountDetails(long sessionID) throws InvalidSessionException {
    // Finding account details based on sessions
    for(Session s:sessions){
      if(s.getClientId() == sessionID){
        return s.getAccount();
      }
    }
    throw new InvalidSessionException();
  }

  private Account getAccount(int acnum) throws InvalidAccountException{
    for(Account acc:accounts){
      if(acc.getAccountNumber() == acnum){
        return  acc;
      }
    }
    throw new InvalidAccountException(acnum);
  }

  private boolean checkSessionActive(long sessID) throws InvalidSessionException{
    for(Session s : sessions){
      if(s.getClientId() == sessID && s.isAlive()) {
        System.out.println("Session " + s.getClientId() + " has been running for " + s.getTimeAlive() + "s");
        System.out.println("Time Remaining: " + (s.getMaxSessionLength() - s.getTimeAlive()) + "s");
        return true;
      }

      if(!s.isAlive()) {
        System.out.println("\n Clearing up timed out sessions");
        System.out.println(">> SessionID: " + s.getClientId());
        finishedSessions.add(s);
      }
    }
    System.out.println();

    // removing sessions which are finished
    sessions.removeAll(finishedSessions);

    throw new InvalidSessionException();
  }
}
