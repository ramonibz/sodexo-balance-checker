package com.rrius.notifications;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class PushBulletNotifier implements Notifier {

  private static final String API_URL = "https://api.pushbullet.com/v2/pushes";

  private static final Logger logger = LoggerFactory.getLogger(PushBulletNotifier.class);

  private HttpClient httpClient;

  public PushBulletNotifier() {
    this.httpClient = HttpClients.createMinimal();
  }

  @Override
  public void sendNotification(Notification notification, String accessToken) {
    HttpPost httpPost = new HttpPost(API_URL);
    httpPost.addHeader("Access-Token", accessToken);
    httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    BasicHttpEntity httpEntity = new BasicHttpEntity();
    logger.debug("Sending notification: " + notification.toJson());
    httpEntity.setContent(new ByteArrayInputStream(notification.toJson().getBytes(StandardCharsets.UTF_8)));
    httpPost.setEntity(httpEntity);
    try {
      HttpResponse httpResponse = this.httpClient.execute(httpPost);
      logger.debug(httpResponse.getStatusLine().toString());
    } catch (IOException e) {
      throw new RuntimeException("Error sending push notification", e);
    }

  }
}
