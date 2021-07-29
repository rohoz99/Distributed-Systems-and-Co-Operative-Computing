package Server;

import Interfaces.StatementInterface;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;
import java.util.ArrayList;

public class Statement implements StatementInterface, Serializable {

  private Date startDate, endDate;
  private Account account;
  private List<Transaction> relevantTransactions;


  public Statement(Account account, Date start, Date end){
    this.startDate = start;
    this.endDate = end;
    this.relevantTransactions = new ArrayList<>();
    this.account = account;

  }
  @Override
  public Date getEndDate() {
    return this.endDate;
  }

  @Override
  public String getAccoutName() {
    return account.getUserName();
  }

  @Override
  public int getAccountnum() {
    return this.account.getAccountNumber();
  }

  @Override
  public Date getStartDate() {
    return this.startDate;
  }

  @Override
  public List getTransactions() {
    //Get all the relevantTransactions for parameters
    this.account.getTransactions().stream()
        //filtering transactions
        .filter(transactions -> transactions.getDate().after(this.startDate) && transactions.getDate().before(this.endDate))
        .collect(Collectors.toList())
        .forEach(date -> relevantTransactions.add(date));

    return this.relevantTransactions;
  }
}