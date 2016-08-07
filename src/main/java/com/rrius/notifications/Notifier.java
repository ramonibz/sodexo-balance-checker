package com.rrius.notifications;

public interface Notifier {

  void sendNotification (Notification notification, String accessToken);
}
