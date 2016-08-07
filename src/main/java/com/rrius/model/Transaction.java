package com.rrius.model;

public class Transaction {
  private String description;
  private String date;
  private double value;

  public Transaction() {
  }

  public Transaction(String description, String date, double value) {
    if (description == null) {
      throw new IllegalArgumentException("Arument: 'Description' cannot be null");
    }
    if (date == null) {
      throw new IllegalArgumentException("Arument: 'date' cannot be null");
    }

    this.description = description.trim();
    this.date = date.trim();
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Transaction)) {
      return false;
    }
    Transaction transaction = (Transaction) obj;
    return this.value == transaction.getValue()
        && this.description.equals(transaction.getDescription())
        && this.description.equals(transaction.getDescription());
  }

  @Override
  public String toString() {
    return "Date: " + this.date + ", Description: " + this.description + ", Value: " + this.value;
  }
}
