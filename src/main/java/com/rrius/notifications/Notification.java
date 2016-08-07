package com.rrius.notifications;

/**
 * Created by ramon on 5/8/16.
 */
public class Notification {

  private String title;
  private String body;
  private String type = "note";

  public Notification(String title, String body) {
    this.title = title;
    this.body = body;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  public String toJson() {
    return "{\"body\":\"" + this.body + "\", \"title\":\"" + this.title + "\",\"type\":\"" + this.type + "\"}";
  }
}
