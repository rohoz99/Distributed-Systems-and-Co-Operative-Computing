package Server;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Account implements Serializable{
// Account instantiation
private int accountNo;
  private List<Transaction> transactions;
  private static int newAccNo = 17461856;
private String username,password;
  private double balance;


  public Account(String userN, String pword){
    this.username = userN;
    this.password = pword;
    this.balance = 0;
    this.accountNo = newAccNo;
    newAccNo++; // Updating the account number

  }
  public String getUserName() {
    return username;
  }

  public void setUsername(String uName){
    this.username = uName;
  }

  public String getPassword(){
    return password;
  }

  public void setPassword(String p) {
    this.password = p;
  }
  public double getBalance() {
    return this.balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public int getAccountNumber() {
    return this.accountNo;
  }

  public void setAccountNumber(int accountNumber) {
    this.accountNo = accountNumber;
  }

  public List<Transaction> getTransactions(){
    return this.transactions;
  }

  public void addTransaction(Transaction t) {
    this.transactions.add(t);
  }

  @Override
  public String toString() {
    return this.accountNo + " " + this.balance;
  }


}
