package com.rrius;

import com.rrius.http.SodexoHttpClient;
import com.rrius.notifications.PushBulletNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(SodexoManager.class);

  public static void main(String[] args) throws Exception{
     SodexoManager sodexoManager = new SodexoManager(new SodexoHttpClient(), new PushBulletNotifier());
    sodexoManager.execute();

//    //Scheduled task
//    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//    ScheduledFuture<?> scheduledFuture =scheduler.scheduleAtFixedRate(sodexoManager, 1, 20, TimeUnit.SECONDS);
//
//    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//      @Override
//      public void run() {
//
//        //wait for sodexo to stop
//
//        scheduledFuture.cancel(false);
//
//        try {
//          scheduler.awaitTermination(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//        }
//        logger.info("Terminating.");
//      }
//    }));
//    while (!scheduler.isTerminated()){
//      Thread.sleep(500);
//    }
  }

}
