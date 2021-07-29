package Client;
import Exceptions.*;
import Interfaces.BankInterface;
import Server.Account;
import Server.Statement;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

//Client program, which connects to the bank using RMI and class methods of the remote bank object
public class ATM {
  static int serverAddress, serverPort = 8080, account;
  static String operation, username, password;
  static long sessionID, id=0;
  static double amount;
  static BankInterface bank;
  static Date startDate, endDate;

  public static void main (String args[]) {
    try {
    	  getCommandLineArguments(args);
      //Set up the rmi registry and get the remote bank object from it
      String name = "BankApp";
      Registry registry = LocateRegistry.getRegistry(serverPort);
      bank = (BankInterface) registry.lookup(name);
      System.out.println("\n................\nClient Connected" + "\n...............\n");
    
    } catch (Exception ie){
      ie.printStackTrace();
      System.out.println(ie);
    }
    double balance;

    //Switch based on the operation
    switch (operation){
      case "login":
        try {
          id = bank.login(username, password); //Login with username and password
          Account acc = bank.getAccountDetails(id);
          //Print account details
          System.out.println("--------------------------\nAccount Details:\n--------------------------\n" +
              "Account Number: " + acc.getAccountNumber() +
              "\nSessionID: " + id +
              "\nUsername: " + acc.getUserName() +
              "\nBalance: " + acc.getBalance() +
              "\n--------------------------\n");
          System.out.println("Session active for 5 minutes");
          System.out.println("Use SessionID " + id + " for all other operations");
        } catch (RemoteException e) {
          e.printStackTrace();
        } catch (InvalidLoginException e) {
          e.printStackTrace();
        } catch (InvalidSessionException e) {
          e.printStackTrace();
        }
        break;

      case "deposit":
        try {
          //Make bank deposit and get updated balance
          balance = bank.deposit(account, amount, sessionID);
          System.out.println("Successfully deposited Euro" + amount + " into account " + account);
          System.out.println("New balance: Euro" + balance);
          //Catch exceptions that can be thrown from the server
        } catch (RemoteException e) {
          e.printStackTrace();
        } catch (InvalidSessionException e) {
          System.out.println(e.getMessage());
        }
        break;

      case "withdraw":
        try {
          //Make bank withdrawal and get updated balance
          balance = bank.withdraw(account, amount, sessionID);
          System.out.println("Successfully withdrew E" + amount + " from account " + account +
              "\nRemaining Balance: E" + balance);
          //Catch exceptions that can be thrown from the server
        } catch (RemoteException e) {
          e.printStackTrace();
        } catch (InvalidSessionException e) {
          System.out.println(e.getMessage());
        } catch (InsufficientFundsException e) {
          System.out.println(e.getMessage());
        }
        break;

      case "balance":
        try {
          //Get account details from bank
          Account acc = bank.getBalance(account,sessionID);
          System.out.println("--------------------------\nAccount Details:\n--------------------------\n" +
              "Account Number: " + acc.getAccountNumber() +
              "\nUsername: " + acc.getUserName() +
              "\nBalance: Euro " + acc.getBalance() +
              "\n--------------------------\n");
          //Catch exceptions that can be thrown from the server
        } catch (RemoteException e) {
          e.printStackTrace();
        } catch (InvalidSessionException e) {
          System.out.println(e.getMessage());
        }
        break;

      case "statement":
        Statement s = null;
        try {
          //Get statement for required dates
          s = (Statement) bank.getStatement(account, startDate, endDate, sessionID);

          SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
          System.out.print("-----------------------------------------------------------------------\n");
          System.out.println("Statement for Account " + account + " between " +
              dateFormat.format(startDate) + " and " + dateFormat.format(endDate));
          System.out.print("-----------------------------------------------------------------------\n");
          System.out.println("Date\t\t\tTransaction Type\tAmount\t\tBalance");
          System.out.print("-----------------------------------------------------------------------\n");

          for(Object t : s.getTransactions()) {
            System.out.println(t);
          }
          System.out.print("-----------------------------------------------------------------------\n");
        } catch (RemoteException e) {
          e.printStackTrace();
        } catch (InvalidSessionException e) {
          System.out.println(e.getMessage());
        } catch (StatementException e) {
          System.out.println(e.getMessage());
        } catch (InsufficientFundsException e) {
          e.printStackTrace();
        }
        break;

      default:
        System.out.println("Operation not supported");
        break;
    }
  }

  public static void getCommandLineArguments(String args[]) throws InvalidArgumentException{

    if(args.length < 2) {
      throw new InvalidArgumentException();
    }
    serverPort = Integer.parseInt(args[1]);
    operation = args[2];
    switch (operation){
      case "login":
        username = args[3];
        password = args[4];
        break;
      case "withdraw":
      case "deposit":
        account = Integer.parseInt(args[3]);
        amount = Double.parseDouble(args[4]);
        sessionID = Long.parseLong(args[5]);
        break;
      case "balance":
        account = Integer.parseInt(args[3]);
        sessionID = Long.parseLong(args[4]);
        break;
      case "statement":
        account = Integer.parseInt(args[3]);
        startDate = new Date(args[4]);
        endDate = new Date(args[5]);
        sessionID = Long.parseLong(args[6]);
        break;
    }
  }
}