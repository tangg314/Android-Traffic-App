package teamkhoya.ics414.khoyatraffic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    EditText destText;
    EditText srcText;
    TextView tv;
    LocationManager locationManager;
    double destLat, destLong, srcLat, srcLong;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List <String> list = new ArrayList<String>();
        list.add("freeway");
        list.add("highway");
        list.add("minor highway");
        srcText = (EditText) findViewById(R.id.GetSrcAddr);
        destText = (EditText) findViewById(R.id.GetDestAddr);
        spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> modeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(modeAdapter);

        Button nextButton = (Button)findViewById(R.id.go_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserInput(destText, srcText);
            }
        });
    }

    /**
     * checks if input is correct, if not then tells user error
     */

    public void checkUserInput(EditText destText, EditText srcText){
        if(destText.getText().toString().equals("")){
            destText.setError("Enter Destination");
        }
        else {
            if ((getLatitude(destText.getText().toString()) == 0)) {
                Toast.makeText(MainActivity.this, "Destination is invalid", Toast.LENGTH_LONG).show();
            }
            else{
                destLat = getLatitude(destText.getText().toString());
                destLong = getLongitude(destText.getText().toString());
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMessageNoGps();
                }
                else{
                    if(srcText.getText().toString().equals("")){
                        srcLat = myLocationLatitude();
                        srcLong = myLocationLongitude();
                    }
                    else{
                        srcLat = getLatitude(srcText.getText().toString());
                        srcLong = getLongitude(srcText.getText().toString());
                    }
                    openMap();
                }
            }
        }
    }

    /**
     * OPENS MAP ACTIVITY
     */
    public void openMap(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        if(destText.getText().toString().equals("")){
            //no input
            destText.setError("Enter Destination");
        }
        else{
            //input exists
            String spinnerString = spinner.getSelectedItem().toString();
            intent.putExtra("src", srcText.getText().toString());
            intent.putExtra("dest", destText.getText().toString());
            intent.putExtra("destLat", destLat);
            intent.putExtra("destLong", destLong);
            intent.putExtra("srcLat", srcLat);
            intent.putExtra("srcLong", srcLong);
            intent.putExtra("speedLevel", spinnerString);

            startActivity(intent);
        }
    }

    public void openFAlerts(View view){

        /**
         * HERE FOR TESTING PURPOSE SO DONT HAVE TO GO THROUGH EVERYTHING
         * ALLOWED TO TAKE OUT IF YOU WANT
         */
        Intent intent = new Intent(MainActivity.this, FAlertActivity.class);
        startActivity(intent);
    }

    /*
        GETS THE LATITUDE OF LOCATION
         */
    public double getLatitude(String strAddress) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        double latitude = 0;
        try {
            address = geocoder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return 0;
            }
            Address location = address.get(0);
            latitude = location.getLatitude();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return latitude;
    }

    /*
    GETS LONGITUDE OF THE LOCATION
     */
    public double getLongitude(String strAddress) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        double longitude = 0;
        try {
            address = geocoder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return 0;
            }
            Address location = address.get(0);
            longitude = location.getLongitude();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return longitude;
    }


    /*
    IF SOURCE ADDRESS NOT FOUND TRACKS CURRENT LOCATION
    GETS THE LATITUDE
     */
    public double myLocationLatitude() {
        // Get LocationManager object from System Service LOCATION_SERVICE 
        // Get Current Location 

        Location myLocation = null;
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
        }
        else{
            try{
                myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            }
            catch (SecurityException e){
                 e.printStackTrace();
            }
        }


        // Get latitude of the current location  
        // Get longitude of the current location 
        //  double longitude = myLocation.getLongitude();  
        return myLocation.getLatitude();
    }

    /*
   IF SOURCE ADDRESS NOT FOUND TRACKS CURRENT LOCATION
   GETS THE Longitude
    */
    public double myLocationLongitude() {
        // Get LocationManager object from System Service LOCATION_SERVICE 
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Get Current Location

        Location myLocation = null;
        try{
            myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        // Get latitude of the current location 
        // double latitude = myLocation.getLatitude();  
        // Get longitude of the current location  
        return myLocation.getLongitude();
    }

    public double convertMetersToMiles(double kilo){
        return kilo/1609.344;
    }

    public void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
