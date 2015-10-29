package teamkhoya.ics414.khoyatraffic;

import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String tvDest;
    String tvSrc;
    EditText dEdit;
    EditText sEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent in = getIntent(); //recalling intent
        tvDest = in.getExtras().getString("dest"); //gets user's destination input through intent
        tvSrc = in.getExtras().getString("src"); //gets user's source input through intent
        dEdit = (EditText)findViewById(R.id.Dest);
        sEdit = (EditText)findViewById(R.id.Src);
        sEdit.setText(tvSrc); //sets user's destination to EditText Box
        dEdit.setText(tvDest); //sets user's destination to EditText Box
        Button startTraffic = (Button) findViewById(R.id.btn_start);
        Button futureAlert = (Button)findViewById(R.id.btn_alert);

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
                // DOES SOMETHING STARTS UP NOTIFICATION
            }
        });

        /**
         * FUTURE ALERT FUNCTION HERE
         */

        futureAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, FAlertActivity.class);
                intent.putExtra("src", tvSrc);
                intent.putExtra("dest", tvDest);

                startActivity(intent);
            }
        });
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

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        return latLng;
    }
}
