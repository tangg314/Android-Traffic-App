package teamkhoya.ics414.khoyatraffic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Button;

import java.util.Calendar;

/*
GOES TO FUTURE ALERT PAGE
 */

public class FAlertActivity extends Activity {
    EditText mEdit;
    EditText TimeEdit;
    Button saveButton;
    int year_x, day_x, month_x;
    Calendar calendar = Calendar.getInstance();
    String tvDest;
    String tvSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falert);
        Intent in = getIntent(); //recalling intent
        tvDest = in.getExtras().getString("dest"); //gets user's destination input through intent
        tvSrc = in.getExtras().getString("src"); //gets user's source input through intent
        EditText dEdit = (EditText)findViewById(R.id.fa_dest);
        EditText sEdit = (EditText)findViewById(R.id.fa_src);
        sEdit.setText(tvSrc); //sets user's destination to EditText Box
        dEdit.setText(tvDest); //sets user's destination to EditText Box

        /*
        SETS DATE
         */
        mEdit = (EditText) findViewById(R.id.DateText); // GETS DATE EDIT BOX
        mEdit.setInputType(InputType.TYPE_NULL); // ELIMINATES THE KEYBOARD SO DIALOG CAN POP UP INSTEAD
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year_x = calendar.get(Calendar.YEAR); //sets YEAR on Date Dialog (aka Calendar)
                month_x = calendar.get(Calendar.MONTH); //sets MONTH on Date Dialog
                day_x = calendar.get(Calendar.DAY_OF_MONTH); //sets DAY on Date Dialog

                DatePickerDialog DatePicker = new DatePickerDialog(FAlertActivity.this, new OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        mEdit.setText(selectedMonth + "/" + selectedDay + "/" + selectedYear); //SETS DATE TO EDITTEXT BOX
                    }
                }, year_x, month_x, day_x);
                DatePicker.setTitle("Select date");
                DatePicker.show(); //SHOWS DATE DIALOG
            }
        });

        /*
        SETS TIME
         */
        TimeEdit = (EditText)findViewById(R.id.TimeText);
        TimeEdit.setInputType(InputType.TYPE_NULL);
        TimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);  // sets the hour on the Time Dialog
                int minute = calendar.get(Calendar.HOUR_OF_DAY); //sets the time on the Time Dialog
                TimePickerDialog TimePicker;
                TimePicker = new TimePickerDialog(FAlertActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        TimeEdit.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//12-Hour
                TimePicker.setTitle("Select Time");
                TimePicker.show(); //shows Time dialog
            }
        });

        saveButton = (Button)findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                NEED TO FIGURE OUT HOW TO SAVE THIS INFORMATION
                INSERT CODE TO SAVE INFORMATION

                 */

                // Goes back to Main Activity
                Intent intent = new Intent(FAlertActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
