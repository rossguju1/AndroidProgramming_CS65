package edu.dartmouth.cs.myruns2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ManualInputActivity extends AppCompatActivity {
    TextView activityDate, activityTime, activityDuration, activityDistance, activityCalorie, activityHeartbeat, activityComment, activityCommentContent;
    LinearLayout activityDurationLayout, activityCalorieLayout, activityDistanceLayout, activityHeartbeatLayout;
    public Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create action bar, set title, and add back button
        ActionBar actionBar = getSupportActionBar();
        //See override at bottom for back button code
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Manual Entry Activity");
        setContentView(R.layout.activity_manual_entry);
        //get the current intent
        Intent intent = getIntent();
        //get the attached extras from the intent i.e the activity name
        String activity_name = ((Intent) intent).getStringExtra("activity_name");
        TextView mName = (TextView) findViewById(R.id.activityName);
        activityDate = (TextView) findViewById(R.id.activityDate);
        activityTime = (TextView) findViewById(R.id.activityTime);
        activityDuration = (TextView) findViewById(R.id.activityDuration);
        activityDistance = (TextView) findViewById(R.id.activityDistance);
        activityCalorie = (TextView) findViewById(R.id.activityCalorie);
        activityHeartbeat = (TextView) findViewById(R.id.activityHeartbeat);
        activityComment = (TextView) findViewById(R.id.activityComment);
        activityCommentContent = (TextView) findViewById(R.id.activityCommentContent);
        activityDurationLayout = (LinearLayout) findViewById(R.id.activityDurationLayout);
        activityDistanceLayout = (LinearLayout) findViewById(R.id.activityDistanceLayout);
        activityCalorieLayout = (LinearLayout) findViewById(R.id.activityCalorieLayout);
        activityHeartbeatLayout = (LinearLayout) findViewById(R.id.activityHeartbeatLayout);

        //Here we check if mName exists and then set appropriate activity name
        if(mName != null){
            mName.setText(activity_name);
        }

        //Add our date selector
        activityDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ManualInputActivity.this,R.style.DateTheme, datePicker, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Add our time selector
        activityTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog partyTimePicker = new TimePickerDialog(ManualInputActivity.this,R.style.DateTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int partyHour, int partyMinute) {
                        activityTime.setText( partyHour + ":" + partyMinute);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                partyTimePicker.show();
            }
        });

        //Add our time selector
        activityDurationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder durationDialog = new AlertDialog.Builder(ManualInputActivity.this);
                durationDialog.setTitle("Duration");

                // Set up the input
                final EditText input = new EditText(ManualInputActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                durationDialog.setView(input);

                // Set up the buttons
                durationDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String duration = (input.getText().toString().equals("")) ? "0" : input.getText().toString();
                        activityDuration.setText(duration);
                    }
                });
                durationDialog.show();
            }
        });

        //Add our time selector
        activityDistanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder distanceDialog = new AlertDialog.Builder(ManualInputActivity.this);
                distanceDialog.setTitle("Distance");

                // Set up the input
                final EditText input = new EditText(ManualInputActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                distanceDialog.setView(input);

                // Set up the buttons
                distanceDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String distance = (input.getText().toString().equals("")) ? "0" : input.getText().toString();
                        activityDistance.setText(distance);
                    }
                });
                distanceDialog.show();
            }
        });

        //Add our calorie text dialog
        activityCalorieLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder calorieDialog = new AlertDialog.Builder(ManualInputActivity.this);
                calorieDialog.setTitle("Calorie");

                // Set up the input
                final EditText input = new EditText(ManualInputActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                calorieDialog.setView(input);

                // Set up the buttons
                calorieDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String calorie = (input.getText().toString().equals("")) ? "0" : input.getText().toString();
                        activityCalorie.setText(calorie);
                    }
                });
                calorieDialog.show();
            }
        });

        //Add our time selector
        activityHeartbeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder heartDialog = new AlertDialog.Builder(ManualInputActivity.this);
                heartDialog.setTitle("Heartbeat");

                // Set up the input
                final EditText input = new EditText(ManualInputActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                heartDialog.setView(input);

                // Set up the buttons
                heartDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String heartbeat = (input.getText().toString().equals("")) ? "0" : input.getText().toString();
                        activityHeartbeat.setText(heartbeat);
                    }
                });
                heartDialog.show();
            }
        });

        //Add our time selector
        activityComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder commentDialog = new AlertDialog.Builder(ManualInputActivity.this);
                commentDialog.setTitle("Comment");

                // Set up the input
                final EditText input = new EditText(ManualInputActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                commentDialog.setView(input);

                // Set up the buttons
                commentDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityCommentContent.setText(input.getText().toString());
                    }
                });
                commentDialog.show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateView();
        }
    };

        private void updateDateView(){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Log.d("SETTING TEXT HERE: ",sdf.format(cal.getTime()));
            activityDate.setText(sdf.format(cal.getTime()));
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ( item.getItemId() ) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
