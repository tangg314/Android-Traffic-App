package teamkhoya.ics414.khoyatraffic;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

//Listens to widget clicks
public class WidgetReceiver extends BroadcastReceiver {
    public WidgetReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName myWidget = new ComponentName(context.getApplicationContext(), kwidget.class);
        int[] allMyWidgetIds = appWidgetManager.getAppWidgetIds(myWidget);

        //update all widgets
        for(int i = 0; i < allMyWidgetIds.length; i++){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kwidget);
            appWidgetManager.updateAppWidget(allMyWidgetIds[i], views);

            //launch khoya traffic
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }
}
