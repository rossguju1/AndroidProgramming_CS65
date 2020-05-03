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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.dartmouth.cs.myruns2.models.Exercise;
import edu.dartmouth.cs.myruns2.models.MyGlobals;
import edu.dartmouth.cs.myruns2.ExerciseEntry;

import static edu.dartmouth.cs.myruns2.RegisterProfileActivity.INTENT_FROM;

public class ManualInputActivity extends AppCompatActivity {
    public static final String MANUAL_INTENT_FROM = "manual_from";
    private int current_tab = -1;
    TextView mName, activityDate, activityTime, activityDuration, activityDistance, activityCalorie, activityHeartbeat, activityComment, activityCommentContent;
    LinearLayout activityDurationLayout, activityCalorieLayout, activityDistanceLayout, activityHeartbeatLayout;


    private Long id;
    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    //private Calendar mDateTime;    // When does this entry happen
    public Calendar cal = Calendar.getInstance();
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private int mCalories;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private int mPrivacy;
    private String mLocationList; // Location list
    private Exercise mExercise;
    private int _hour;
    private int _minute;
    private int _day;
    private int _month;
    private int _year;
    SimpleDateFormat _sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    String dynamic_date;
    String dynamic_time;

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
        mName = (TextView) findViewById(R.id.activityName);
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



// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//        Date date = new Date();
//        sdf.format(date);


//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//        String dateInString = "31-08-1982 10:20:56";
//        Date date = sdf.parse(dateInString);

//// Date -> Calendar
//        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy hh:mm");
//        String dateInString = "01/22/2015 10:20";
//        Date date = null;
//        try {
//            date = sdf.parse(dateInString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);



        //Here we check if mName exists and then set appropriate activity name
        if(mName != null){
            mName.setText(activity_name);
        }

        //Add our date selector
        activityDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //dynamic_date = month + "/" + day + "/" + year;
                new DatePickerDialog(ManualInputActivity.this,
                        R.style.DateTheme,
                        datePicker,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();

              //  Log.d("tetsing date: ", dynamic_date);

            }

        });

        //Add our time selector
        activityTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog partyTimePicker = new TimePickerDialog(
                        ManualInputActivity.this,
                        R.style.DateTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int partyHour, int partyMinute) {
                                activityTime.setText( partyHour + ":" + partyMinute);
                                _hour =  partyHour;
                                _minute = partyMinute;
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true);

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
                        //mExercise.setmDuration(Integer.valueOf(duration);

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
            _year = year;
            _month = monthOfYear;
            _day = dayOfMonth;

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

        int id = item.getItemId();
        switch ( id ) {

            case android.R.id.home:
                Toast.makeText(getApplicationContext(),
                        "Moved Back",
                        Toast.LENGTH_SHORT).show();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.ManualEntryBttn:
                if (current_tab == 0){


                    //mExercise.setmDateTime(dynamic_date + " " + dynamic_time);
                    saveManualEntry();
                    Toast.makeText(getApplicationContext(),
                            "Saved",
                            Toast.LENGTH_SHORT).show();

                    finish();
                    //database save entry
                } else if(current_tab == 1){
                    //database delete entry
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manual_entry_menu, menu);
        //Set the appropriate button title depending on navigation context
        if(getIntent().getStringExtra(MANUAL_INTENT_FROM).equals("start_tab")){
            current_tab = 0;
            menu.getItem(0).setTitle("SAVE");
        }else if (getIntent().getStringExtra(MANUAL_INTENT_FROM).equals("history_tab")){
            current_tab = 1;
            menu.getItem(0).setTitle("DELETE");
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void saveManualEntry(){
        MyGlobals globs = new MyGlobals();
        mExercise = new Exercise();

        String time = _hour + ":" + _minute;
        String date = _month + "/" + _day + "/" + _year;


        String date_time = date + " " + time;

        String activity_name = mName.getText().toString();
        String duration = activityDuration.getText().toString();
        String distance = activityDistance.getText().toString();

        String calories = activityCalorie.getText().toString();

        String heartbeat = activityHeartbeat.getText().toString();
        String comment = activityCommentContent.getText().toString();

        int input = globs.getValue("INPUT_TYPES", "Manual");

        int activity = globs.getValue("ACTIVITY_TYPES", activity_name);


        mExercise.setmInputType(input);
        mExercise.setmActivityType(activity);
        mExercise.setmDateTime(date_time);
        mExercise.setmDuration(Integer.parseInt(duration));
        mExercise.setmDistance(Double.parseDouble(distance));
        mExercise.setmCalories(Integer.parseInt(calories));
        mExercise.setmHeartRate(Integer.parseInt(heartbeat));
        mExercise.setmComment(comment);




        ExerciseEntry mEntry =  new ExerciseEntry(this);
        mEntry.open();
        mEntry.insertEntry(mExercise);
        mEntry.close();

        Log.d("<SAVE MANUAL ENTRY>",
                    "input type: " + input
                    + " Activity type: " + activity
                    + " Date and time: " + date_time
                    + " Duration: " + duration
                    + " Distance: " + distance
                    + " Calories: " + calories
                    + " Heartrate: " + heartbeat
                    + " Comments: " + comment
            );







    }



}
