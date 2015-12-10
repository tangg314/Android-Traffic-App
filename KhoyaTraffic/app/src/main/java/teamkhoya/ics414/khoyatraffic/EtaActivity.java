package teamkhoya.ics414.khoyatraffic;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class EtaActivity extends AppCompatActivity {
    private String TAG = EtaActivity.class.getSimpleName();
    NotifLight newLight = new NotifLight();
    private NotificationManager notify = null;
    TextView etaText;
    String url;
    String speedType;
    TrafficAsync etaTrafficTask = null;
    TrafficAsync etaTrafficTask2 = null;

    //interval variables
    //milliseconds
    long interval = 6000;
    long totalTime = 360000;
    //output criteria, minimum speed accepted as output
    //mph
    double minSpeed = 0;
    protected static final String ACTION_ON_CLICK = "android.kwidget.ACTION_ON_CLICK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        etaText = (TextView) findViewById(R.id.eta_display);
        Intent in = getIntent(); //recalling intent
        url = in.getExtras().getString("url");
        // Toast.makeText(EtaActivity.this, url, Toast.LENGTH_LONG).show();
        speedType = in.getExtras().getString("speedType");
        if(speedType.equals("freeway") ){
            minSpeed = 50;
        }
        else if(speedType.equals("highway")){
            minSpeed = 35;
        }
        else if(speedType.equals("minor highway")){
            minSpeed = 25;
        }
        else{
            minSpeed = 30;
        }
        etaTrafficTask = new TrafficAsync();
        etaTrafficTask.execute(url);
        final Button stop_button = (Button) findViewById(R.id.stop_eta_button);


        //repeatedly gets data every interval for totalTime
        new CountDownTimer(totalTime, interval) {
            public void onTick(long millisUntilFinished) {

                stop_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EtaActivity.this.onStop();
                        cancel();
                        Intent intent = new Intent(EtaActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                if(etaTrafficTask.getStatus() != AsyncTask.Status.RUNNING){
                    etaTrafficTask2 = new TrafficAsync();
                    etaTrafficTask2.execute(url);
                }

                else{
                    etaTrafficTask.cancel(true);
                }
            }

            public void onFinish() {}
        }.start();
    }

    public void printSpeed(String speed){
        TextView tv = (TextView)findViewById(R.id.eta_display);
        tv.setText(speed);
    }

    private class TrafficAsync extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String str_dist="";
            String str_time="";

            try {
                URL url = new URL(params[0]);
                URLConnection uconnect = url.openConnection();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(uconnect.getInputStream()));
                String line;
                StringBuffer sb = new StringBuffer();
                while((line = bReader.readLine()) != null){
                    sb.append(line);
                }

                JSONObject main = new JSONObject(sb.toString());
                JSONArray rows_array = new JSONArray(main.getString("rows"));
                JSONObject elements_object = rows_array.getJSONObject(0);
                JSONArray elements_array = elements_object.getJSONArray("elements");
                JSONObject distance_object = elements_array.getJSONObject(0);
                JSONObject distance = distance_object.getJSONObject("distance");
                str_dist = distance.getString("value"); //distance in meters
                //str_dist = distance.getString("text"); // distance string in km

                JSONObject time = distance_object.getJSONObject("duration");
                str_time = time.getString("text"); //duration string in mins
                //str_time = time.getString("value"); //duration in seconds
            } catch (java.io.IOException | JSONException e) {
                e.printStackTrace();
            }
            return str_dist + ":" + str_time;
        }

        @Override
        protected void onPostExecute(String result){
            String[] arr = result.split(":");
            DecimalFormat nfm = new DecimalFormat("#0.00");
            double miles = convertMetersToMiles(Double.parseDouble(arr[0]));

            //retrieve minutes from data
            int end = arr[1].indexOf(" mins");
            Log.d(TAG, "arr[1] = " + arr[1].substring(0, end));
            double minutes = Double.parseDouble(arr[1].substring(0, end));
            double hours = convertMinutesToHours(minutes);

            //String printText = "Distance: " + String.valueOf(nfm.format(miles))+ " miles\nduration:" + hours;
            //etaDis.setText(printText); need to make EditText or TextView whatever you prefer to print out the information

            Log.d(TAG, "miles = " + miles);
            Log.d(TAG, "time = " + minutes + " min");
            Log.d(TAG, "time = " + hours + " hr");

            //speed
            double mph = miles / hours;

            //int to send to notification light
            int trafficInt = 0;

//            //current time
//            Calendar calendar = Calendar.getInstance();
//            int hour = calendar.get(Calendar.HOUR);
//            int minute = calendar.get(Calendar.MINUTE);
//            int second = calendar.get(Calendar.SECOND);
            //check whether to output speed
            if(mph > (minSpeed - 5)){
                printSpeed("Speed: " + (int)mph + " mph" + "\nETA: "+ (int)minutes + " min");
                trafficInt = 3;
            }
            else if((minSpeed - 10) < mph && mph <= (minSpeed -5)){
                printSpeed("Speed: " + (int)mph + " mph" + "\nETA: "+ (int)minutes + " min");
                trafficInt = 2;
            }
            else{
                printSpeed("The speed is not acceptable");
                trafficInt = 1;
            }

            /*
            if(mph < minSpeed){
                printSpeed("The speed is not acceptable");
                trafficInt = 1;
                //newLight.redNotification(notify);
            }
            else if(mph == minSpeed){
                printSpeed("Speed: " + (int)mph + " mph" + "\nETA: "+ (int)minutes + " min");
                trafficInt = 2;
                //newLight.yellowNotification(notify);
            }
            else if(mph > minSpeed){
                printSpeed("Speed: " + (int)mph + " mph" + "\nETA: "+ (int)minutes + " min");
                trafficInt = 3;
                //newLight.greenNotification(notify);
            }
*/
            //set widget icon and notification light based on speed int
            NotifLight.decideLight(trafficInt, notify);
            decideIcon(trafficInt, EtaActivity.this);
            Log.d(TAG, "speed = " + mph + " mph");
        }
    }

    /**
     * very simple to understand
     * it takes the meters from the json file and converts it to miles
     * @param kilo
     * @return
     */
    public double convertMetersToMiles(double kilo){
        return kilo/1609.344;
    }

    /**
     * May need to make a method for converting seconds to hours
     * takes the seconds from the json file and converts it to hours
     * @param seconds
     * @return hours
     */
    public double convertSecondsToHours(double seconds) {
        return seconds/3600;
    }

    /**
     * takes the minutes from the json file and converts it to hours
     * @param minutes
     * @return hours
     */
    public double convertMinutesToHours(double minutes) {
        return minutes/60;
    }

    /**
     * converts minutes to msec
     * @param minutes
     * @return
     */
    public Long convertMinutesToMilisec(double minutes) {
        return Double.doubleToLongBits(minutes) * 60000;
    }

    //displays widget icon based on speed int
    public void decideIcon(int speedInt, Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kwidget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisWidget = new ComponentName(getApplicationContext(),kwidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        //red arrow
        if(speedInt == 1){
            if(views == null){
                Log.d("kwidget", "trafficview is null");
            }
            views.setImageViewResource(R.id.trafficIcon, R.drawable.icon1);
        }

        //yellow arrow
        else if(speedInt == 2){
            views.setImageViewResource(R.id.trafficIcon, R.drawable.icon2);
        }

        else if(speedInt == 3){
            views.setImageViewResource(R.id.trafficIcon, R.drawable.icon3);
        }

        //default, white arrow
        else{
            views.setImageViewResource(R.id.trafficIcon, R.drawable.icon0);
        }

        // Instruct the widget manager to update all widgets
        for(int i = 0; i < appWidgetIds.length; i++){
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        if(etaTrafficTask.getStatus() == AsyncTask.Status.RUNNING){
            etaTrafficTask.cancel(true);
        }

        if(etaTrafficTask2.getStatus() == AsyncTask.Status.RUNNING){
            etaTrafficTask2.cancel(true);
        }

        newLight.clearNotification(notify);
        //reset widget icons
        decideIcon(-1, this);
    }
}

