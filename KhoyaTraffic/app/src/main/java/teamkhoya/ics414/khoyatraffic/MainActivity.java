package teamkhoya.ics414.khoyatraffic;


import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    private NotificationManager notifyMgr = null;
    EditText destText;
    EditText srcText;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        srcText = (EditText) findViewById(R.id.GetSrcAddr);
        destText = (EditText) findViewById(R.id.GetDestAddr);
        destText.setOnKeyListener(new OnKeyListener() {
            @Override
            //WHEN USER PRESSES ENTER IT GOES TO THE NEXT ACTIVITY
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    //perfom action on keypress
                    openMap();
                    return true;
                }
                return false;
            }
        });
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
            intent.putExtra("src", srcText.getText().toString());
            intent.putExtra("dest", destText.getText().toString());
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
}
