package Server;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


//this allows the session to cancelled after 5 mins
public class Session extends TimerTask implements Serializable{
  public long sessionId;
  private int timeAlive;
  private Timer timer;
  private volatile boolean alive;
  private Account account;

  private static final int MAX_SESSION_LENGTH = 60 * 5;
  private static final long DELAY = 1000;

  public Session(Account account) {

    this.timer = new Timer();
    this.startTimer();
    this.sessionId = (int)(Math.random()*900000)+100000; // random sesh id
    this.account = account;
    this.alive = true;
    this.timeAlive = 0;
    System.out.println("Session " + sessionId + " created\n");
  }

  private void startTimer() {
    this.timer.scheduleAtFixedRate(this, new Date(System.currentTimeMillis()), DELAY);
  }

  @Override
  public void run() {
    this.timeAlive++;
    if(this.timeAlive == MAX_SESSION_LENGTH) {
      //set alive to false and cancel the timer
      this.alive = false;
      this.timer.cancel();
      System.out.println("\n---------------------------\nSession " + this.sessionId + " terminated \n---------------------------");
      System.out.println(this);
      System.out.println("---------------------------");
    }
  }


  public long getClientId(){
    return this.sessionId;
  }

  public int getTimeAlive(){
    return this.timeAlive;
  }

  public int getMaxSessionLength(){
    return MAX_SESSION_LENGTH;
  }

  public Account getAccount(){
    return this.account;
  }

  public boolean isAlive() {
    return this.alive;
  }


  @Override
  public String toString() {
    return "Account: " + this.account.getAccountNumber() + "\nSessionID: " +
        this.sessionId +"\nTime Alive: " + this.timeAlive + "\nAlive: " + this.alive;
  }
}