package com.rrius.http.parsers;


import com.rrius.model.Transaction;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SodexoTransactionsParser {

  private Document document;

  public SodexoTransactionsParser(Document document) {
    this.document = document;
  }

  public Transaction getLastTransaction() {
    Elements trElement = this.document.getElementsByTag("tr");
    String date = trElement.get(1).childNode(1).childNode(0).toString();
    String description = trElement.get(1).childNode(5).childNode(0).toString();
    String strValue = trElement.get(1).childNode(7).childNode(0).toString();
    Double value =  Double.valueOf(strValue.substring(0, strValue.indexOf(" ")));
    return new Transaction(description, date, value);
  }



}
