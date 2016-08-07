package com.rrius.http.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class SodexoCardDetailParser {

  private Document document;

  public SodexoCardDetailParser(Document document) {
    this.document = document;
  }

  public Double getCurrentBalance(String cardId) {
    Element element =this. document.getElementById("resumen-" + cardId);
    Node balanceNode = element.getElementById("tarjeta-SaldoDisponible").childNode(0);
    String strBalance = balanceNode.toString().trim();
    return Double.valueOf(strBalance.substring(0, strBalance.indexOf(" ")));
  }

  public String getEncryptedCardId(String cardId) {
    Element formMovimientos = this.document.getElementById("formMovimientos-" + cardId);
    Elements elements = formMovimientos.getElementsByAttributeValue("name", "idcard");
    return elements.get(0).attr("value").trim();
  }

  public String getEncryptedCardNumber(String cardId) {
    Element formMovimientos = this.document.getElementById("formMovimientos-" + cardId);
    Elements elements = formMovimientos.getElementsByAttributeValue("name", "cardnumber");
    return elements.get(0).attr("value").trim();
  }

}
