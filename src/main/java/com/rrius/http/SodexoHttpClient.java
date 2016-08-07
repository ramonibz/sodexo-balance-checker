package com.rrius.http;

import com.rrius.http.exceptions.CardDetailException;
import com.rrius.http.exceptions.LastTransactionsException;
import com.rrius.http.exceptions.LoginException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;



public class SodexoHttpClient {

  private final Logger logger = LoggerFactory.getLogger(SodexoHttpClient.class);

  private static final String URL_LOGIN_POST = "http://www.mysodexo.es/lib/login.php";
  private static final String URL_CARD_DETAIL = "http://www.mysodexo.es/mis-servicios-tarjetas/restaurante/";
  private static final String URL_CARD_MOVEMENTS  = "http://www.mysodexo.es/lib/tarjetas_movimientos.php";

  private HttpClient httpClient;

  public SodexoHttpClient() {
    this.httpClient = HttpClients.createDefault();
  }

  public void tryLogin(String username, String password) {
    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("usr", username));
    nvps.add(new BasicNameValuePair("pwd-login", password));
    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);

    HttpPost httpPost = new HttpPost(URL_LOGIN_POST);
    httpPost.setEntity(urlEncodedFormEntity);
    addCommonRequestHeaders(httpPost);
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

    try {
      HttpResponse httpResponse = this.httpClient.execute(httpPost);

      ///Assert response code is a redirection
      if (httpResponse.getStatusLine().getStatusCode()!= 302) {
        throw new LoginException("Login returned unexpected status code. Expected 302, returned: "
        + httpResponse.getStatusLine().getStatusCode());
      }

    } catch (IOException e){
      throw new LoginException("Error during login process", e);
    }

  }

  public Document getCardDetailDocument(String cardId) {
    HttpGet httpGet = new HttpGet(URL_CARD_DETAIL.concat(cardId));
    try {
      HttpResponse httpResponse = this.httpClient.execute(httpGet);
      this.logger.debug("Response from Card Detail module: " + httpResponse.getStatusLine());
      return Jsoup.parse(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8.toString(), "http://www.mysodexo.es/");
    } catch (IOException e) {
      throw new CardDetailException("Error retrieving Card detail info. ", e);
    }
  }

  public Document getLastTransactionsDocument(String cardNumber, String idCard) {
    HttpPost httpPost = new HttpPost(URL_CARD_MOVEMENTS);

    List<NameValuePair> nvps = new ArrayList<>();

    nvps.add(new BasicNameValuePair("cardnumber", cardNumber));
    nvps.add(new BasicNameValuePair("idcard", idCard));
    nvps.add(new BasicNameValuePair("idproduct", "33")); //TODO Review to retrieve this dynamically
    nvps.add(new BasicNameValuePair("esBusiness", "0")); //TODO Review to retrieve this dynamically
    nvps.add(new BasicNameValuePair("fechaIni", ""));
    nvps.add(new BasicNameValuePair("fechaFin", ""));
    nvps.add(new BasicNameValuePair("num_movimientos", ""));
    nvps.add(new BasicNameValuePair("tipo", ""));

    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);

    httpPost.setEntity(urlEncodedFormEntity);

    addCommonRequestHeaders(httpPost);

    try {
      HttpResponse httpResponse = this.httpClient.execute(httpPost);
      logger.debug(httpResponse.getStatusLine().toString());
      return Jsoup.parse(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8.toString(), "http://www.mysodexo.es/");
    } catch (IOException e){
      throw new LastTransactionsException("Error retrieving Last transactions document", e);
    }
  }


  private static void addCommonRequestHeaders(AbstractHttpMessage httpMethod) {
    httpMethod.addHeader("Accept", "*/*");
    httpMethod.addHeader("Referer", "http://www.mysodexo.es/");
    httpMethod.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");

  }
}