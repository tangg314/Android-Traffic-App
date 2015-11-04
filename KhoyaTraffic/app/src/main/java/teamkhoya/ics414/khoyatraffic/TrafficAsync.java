package teamkhoya.ics414.khoyatraffic;

import android.os.AsyncTask;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by asfagaragan on 11/4/15.
 */
public class TrafficAsync extends AsyncTask<String, Void, String> {
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
        String printText = "Distance: " + String.valueOf(nfm.format(miles))+ " miles\nduration: " + arr[1];
        //etaDis.setText(printText); need to make EditText or TextView whatever you prefer to print out the information
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
     */
}