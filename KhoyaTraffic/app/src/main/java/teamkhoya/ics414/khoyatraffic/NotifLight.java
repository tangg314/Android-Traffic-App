package teamkhoya.ics414.khoyatraffic;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Handler;

/**
 * Created by asf on 10/24/15.
 * Makes the notification light turn on and off
 * NEED TO FIGURE OUT HOW TO USE THIS
 */



public class NotifLight extends android.app.Activity {
    int NOTIFICATION_ID = 1;
    int LED_ON_MS = 100;
    int LED_OFF_MS = 100;
    Notification notif=new Notification();

    public NotifLight(){

    }

    public void redNotification(NotificationManager notifyMgr){
        notif.ledARGB = Color.RED; //color of light
        notif.flags = Notification.FLAG_SHOW_LIGHTS; //shows light
        notif.ledOnMS = LED_ON_MS; //how long the light stays on
        notif.ledOffMS = LED_OFF_MS; // how long the light stays off
        notifyMgr.notify(NOTIFICATION_ID, notif);
        // Program the end of the light :
        Handler mCleanLedHandler = new Handler();

        mCleanLedHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

            }
        }, 500);
    }

    public void yellowNotification(NotificationManager notifyMgr){
        Notification notif = new Notification();
        notif.ledARGB = Color.YELLOW;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = LED_ON_MS;
        notif.ledOffMS = LED_OFF_MS;
        notifyMgr.notify(NOTIFICATION_ID, notif);
        // Program the end of the light :
        Handler mCleanLedHandler = new Handler();

        mCleanLedHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

            }
        }, 500);
    }

    public void greenNotification(NotificationManager notifyMgr){
        Notification notif = new Notification();
        notif.ledARGB = Color.GREEN;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = LED_ON_MS;
        notif.ledOffMS = LED_OFF_MS;
        notifyMgr.notify(NOTIFICATION_ID, notif);
        // Program the end of the light :
        Handler mCleanLedHandler = new Handler();

        mCleanLedHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

            }
        }, 500);
    }


    public void clearNotification(NotificationManager notifyMgr) {
        //Clear the notification
        notifyMgr.cancel(NOTIFICATION_ID);
    }

}
