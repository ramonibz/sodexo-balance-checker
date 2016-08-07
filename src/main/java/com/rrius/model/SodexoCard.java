package com.rrius.model;

public class SodexoCard {

  private String id;
  private double balance;
  private Transaction lastTransaction;

  public SodexoCard() {
  }

  public SodexoCard(String id, double balance, Transaction transaction) {
    this.id = id.trim();
    this.balance = balance;
    this.lastTransaction = transaction;
  }

  public Transaction getLastTransaction() {
    return lastTransaction;
  }

  public void setLastTransaction(Transaction lastTransaction) {
    this.lastTransaction = lastTransaction;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof SodexoCard)) {
      return false;
    }
    SodexoCard card = (SodexoCard) obj;
    return this.lastTransaction.equals(card.getLastTransaction())
        && this.id.equals(card.getId())
        && this.balance == card.getBalance();
  }

  @Override
  public String toString() {
    return "Id: " + this.id + ", Balance: " + this.balance + "\nLast transaction: "
        + this.lastTransaction.toString();
  }



}
