package teamkhoya.ics414.khoyatraffic;

import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{


    private GoogleMap mMap;
    String tvDest;
    String tvSrc;
    EditText dEdit;
    EditText sEdit;

    static double destLat;
    static double destLong;
    static double srcLat;
    static double srcLong;
    String url;
    EtaAsync etaAsync = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final Intent in = getIntent(); //recalling intent
        tvDest = in.getExtras().getString("dest"); //gets user's destination input through intent
        tvSrc = in.getExtras().getString("src"); //gets user's source input through intent
        destLat = in.getExtras().getDouble("destLat");
        destLong = in.getExtras().getDouble("destLong");
        srcLat = in.getExtras().getDouble("srcLat");
        srcLong = in.getExtras().getDouble("srcLong");
        dEdit = (EditText)findViewById(R.id.Dest);
        sEdit = (EditText)findViewById(R.id.Src);
        url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + srcLat + "," + srcLong + "&destinations=" + destLat + "," + destLong + "&mode=driving";
        etaAsync = new EtaAsync();
        etaAsync.execute(url);
        sEdit.setText(tvSrc); //sets user's destination to EditText Box
        dEdit.setText(tvDest); //sets user's destination to EditText Box
        Button startTraffic = (Button) findViewById(R.id.btn_start);
        //Button futureAlert = (Button)findViewById(R.id.btn_alert);
        /**
         * RESET DIRECTIONS
         */

        dEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            //WHEN USER PRESSES ENTER IT GOES TO THE NEXT ACTIVITY
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    //perfom action on keypress
                    resetMap();
                    return true;
                }
                return false;
            }
        });

        /**
         *START BUTTON FUNCTION
         */
        startTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to retrieve the following everytime start button is clicked
//                destLat = MainActivity.getLatitude();
//                destLong = MainActivity.getLongitude();
//                srcLat = ;
//                srcLong = ;

                url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + srcLat + "," + srcLong + "&destinations=" + destLat + "," + destLong + "&mode=driving";
                Intent intent = new Intent(MapsActivity.this, EtaActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("speedType", in.getExtras().getString("speedLevel"));
                startActivity(intent);
            }
        });

        /**
         * FUTURE ALERT FUNCTION HERE


        futureAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, FAlertActivity.class);
                intent.putExtra("src", tvSrc);
                intent.putExtra("dest", tvDest);

                startActivity(intent);
            }
        });
         */
    }

    public void resetMap(){
        Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
        if(dEdit.getText().toString().equals("")){
            //no input
            dEdit.setError("Enter Destination");
        }
        else{
            //input exists
            intent.putExtra("src", sEdit.getText().toString());
            intent.putExtra("dest", dEdit.getText().toString());
            intent.putExtra("speedType", intent.getExtras().getString("speedLevel"));
            startActivity(intent);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng location =  getLocationFromAddress(tvDest); //converts destination input to Latlng

        googleMap.setMyLocationEnabled(true); //sets current location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setTrafficEnabled(true); // turns on trafficLayer
        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        googleMap.addMarker(new MarkerOptions().position(location));
        if(tvSrc.equals("")){
            googleMap.addMarker(new MarkerOptions().position(myCurrentLocation()));
        }
        else{
            googleMap.addMarker(new MarkerOptions().position(getLocationFromAddress(tvSrc)));
        }
    }

    /*
    Gets user input and converts it into LatLng
     */
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = geocoder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return p1;
    }

    /*
     Gets the LatLng of the current location
     */
    public LatLng myCurrentLocation(){
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        Location myLocation = null;

        // Get Current Location
        try{
            myLocation = locationManager.getLastKnownLocation(provider);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }



        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        return latLng;
    }

    private class EtaAsync extends AsyncTask<String, Void, String> {
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
                str_dist = distance.getString("value");

                JSONObject time = distance_object.getJSONObject("duration");
                str_time = time.getString("text");
            } catch (java.io.IOException | JSONException e) {
                e.printStackTrace();
            }
            return str_dist + ":" + str_time;
        }

        @Override
        protected void onPostExecute(String result){
            String[] arr = result.split(":");
            DecimalFormat nfm = new DecimalFormat("#0.00");
            int miles = (int)convertMetersToMiles(Double.parseDouble(arr[0]));

            //retrieve minutes from data
            int end = arr[1].indexOf(" mins");
            double minutes = Double.parseDouble(arr[1].substring(0, end));
            double hours = convertMinutesToHours(minutes);

            double mph = miles / hours;
            TextView etaMapDisplay = (TextView) findViewById(R.id.eta_map_display);
            etaMapDisplay.setText("Distance: "+miles+" miles\nDuration: " + arr[1]);
            etaAsync.cancel(true);

        }

    }
    public double convertMetersToMiles(double kilo){
        return kilo/1609.344;
    }
    public double convertMinutesToHours(double minutes) {
        return minutes/60;
    }
}
