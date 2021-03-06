package Server;

import java.io.Serializable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Date;
public class Transaction implements Serializable {

  //Instance variables
  private Date date;
  private String type;
  private double amount;
  private Account account;
  private double accBalance;

  public Transaction(Account account, String type) {
    this.account = account;
    this.accBalance = 0;
    this.type = type;
    this.date = new Date(System.currentTimeMillis());
  }

  public int getAccountNumber() {
    return this.account.getAccountNumber();
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public double getAmount() {
    return this.amount;
  }
  public Date getDate() {
    return this.date;
  }


  public void setAmount(double amount) {
    double amt = this.account.getBalance();
    this.accBalance = this.type.equals("Deposit") ? this.accBalance = amt + this.amount : amt - this.amount;
    this.amount = amount;
  }

  @Override
  public String toString() {
    //Print transaction details based on type
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    DecimalFormat decForm = new DecimalFormat();
    decForm.setMaximumFractionDigits(2);

    if(this.type.equals("Deposit"))
      return dateFormat.format(this.date) + "\t" + this.type + "\t\t\t" + this.amount + "\t\t" + decForm.format(this.accBalance);
    else
      return dateFormat.format(this.date) + "\t" + this.type + "\t\t" + this.amount + "\t\t" + decForm.format(this.accBalance);
  }
}