package teamkhoya.ics414.khoyatraffic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    TextView etaText;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        etaText = (TextView) findViewById(R.id.eta_display);
        Intent in = getIntent(); //recalling intent
        url = in.getExtras().getString("url");
       // Toast.makeText(EtaActivity.this, url, Toast.LENGTH_LONG).show();
       TrafficAsync etaTrafficTask = new TrafficAsync();
        etaTrafficTask.execute(url);
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
        Long currentTime = System.nanoTime();
        Long estimatedTime = 0L;
        Long waitMS = 60000L; //cycle time

        do {
            try {
                URL url = new URL(params[0]);
                URLConnection uconnect = url.openConnection();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(uconnect.getInputStream()));
                String line;
                StringBuffer sb = new StringBuffer();
                while ((line = bReader.readLine()) != null) {
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

                currentTime = System.currentTimeMillis();
                estimatedTime = currentTime + convertMinutesToMilisec(Long.parseLong(str_time)); //compute eta in msec

            } catch (java.io.IOException | JSONException e) {
                e.printStackTrace();
            }
            try {
                wait(waitMS); //cycle
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (currentTime < estimatedTime);
        return str_dist + ":" + str_time;
    }

    @Override
    protected void onPostExecute(String result){
        String[] arr = result.split(":");
        DecimalFormat nfm = new DecimalFormat("#0.00");
        double miles = convertMetersToMiles(Double.parseDouble(arr[0]));
        double hours = convertMinutesToHours(Double.parseDouble(arr[1]));
        String printText = "Distance: " + String.valueOf(nfm.format(miles))+ " miles\nduration: " + hours;
        //etaDis.setText(printText); need to make EditText or TextView whatever you prefer to print out the information

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
    }

