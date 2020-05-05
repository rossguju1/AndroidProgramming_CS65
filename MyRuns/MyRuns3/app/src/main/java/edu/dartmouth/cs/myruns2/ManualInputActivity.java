package edu.dartmouth.cs.myruns2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ExpandableListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.dartmouth.cs.myruns2.models.Exercise;
import edu.dartmouth.cs.myruns2.models.MyGlobals;
import edu.dartmouth.cs.myruns2.ExerciseEntry;
import edu.dartmouth.cs.myruns2.HistoryFragment;
import static edu.dartmouth.cs.myruns2.RegisterProfileActivity.INTENT_FROM;

public class ManualInputActivity extends AppCompatActivity {
    public static final String MANUAL_INTENT_FROM = "manual_from";
    public static final String DELETE_EXERCISE = "history_from";
    public static final String DELETE_ITEM = "selected";
    private static final String DEBUG_TAG = "ManualInputActivity";
    private int current_tab = -1;
    TextView mName, activityDate, activityTime, activityDuration, activityDistance, activityCalorie,
            activityHeartbeat, activityComment, activityCommentContent, distanceLabel;
    LinearLayout activityDurationLayout, activityCalorieLayout, activityDistanceLayout, activityHeartbeatLayout;

    //Keys to retrieve temp saved data
    private static final String DATE_STATE_KEY = "saved_date";
    private static final String TIME_STATE_KEY = "saved_time";
    private static final String DURATION_STATE_KEY = "saved_duration";
    private static final String DISTANCE_STATE_KEY = "saved_distance";
    private static final String CALORIES_STATE_KEY = "saved_calories";
    private static final String HEART_STATE_KEY = "saved_heart";
    private static final String COMMENT_STATE_KEY = "saved_comment";

    public ExerciseEntry mEntry;
    public String _id;
    public long id;
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
    public MyGlobals globs;
//    SimpleDateFormat _sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private AsyncInsert task = null;
    private AsyncDelete delete_task = null;
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

        distanceLabel = (TextView) findViewById((R.id.distanceLabel));
        globs = new MyGlobals();


        distanceLabel.setText(globs.getValue_str(globs.UNIT_TABLE, globs.CURRENT_UNITS));


        if (getIntent().getStringExtra(MANUAL_INTENT_FROM).equals("history_tab")){
            current_tab = 1;


           _id = getIntent().getStringExtra(DELETE_EXERCISE);

           id = Long.parseLong(_id);



            Log.d("DEBUG", "INSIDE MANUAL FROM *HISTORY* Tab and clicked on ID: " + id );


            mEntry = new ExerciseEntry(this);

            mEntry.open();
           // try {
                Exercise e = mEntry.fetchEntryByIndex(id);

                mName.setText(globs.getValue_str(globs.ACT, e.getmActivityType()));

                String[] splited = e.getmDateTime().split("\\s+");
                if (globs.CURRENT_UNITS == 1) {
                    activityDistance.setText(String.valueOf(KilometersToMiles(e.getmDistance())));
                } else {
                    activityDistance.setText(String.valueOf(e.getmDistance()));
                }

                activityDate.setText(splited[0]);
                activityTime.setText(splited[1]);
                activityDuration.setText(String.valueOf(e.getmDuration()));
                activityCalorie.setText(String.valueOf(e.getmCalories()));
                activityHeartbeat.setText(String.valueOf(e.getmHeartRate()));
                //activityComment = (TextView) findViewById(R.id.activityComment);
                activityCommentContent.setText(e.getmComment());


           // } catch (Exception ee){
                Log.d("DEBUG", "ERROR IN FETCH");


         //   }


            mEntry.close();


        } else {
            Log.d("DEBUG", "INSIDE MANUAL FROM *START* TAB");

            current_tab = 0;

            //Here we check if mName exists and then set appropriate activity name
            if (mName != null) {
                mName.setText(activity_name);
            }

            // Populate with preserved data if lifeCycle was interrupted
            if (savedInstanceState != null) {
                // Get saved values
                String mDate = savedInstanceState.getString(DATE_STATE_KEY);
                String mTime = savedInstanceState.getString(TIME_STATE_KEY);
                String mDistance = savedInstanceState.getString(DISTANCE_STATE_KEY);
                String mDuration = savedInstanceState.getString(DURATION_STATE_KEY);
                String mCalories = savedInstanceState.getString(CALORIES_STATE_KEY);
                String mHeartRate= savedInstanceState.getString(HEART_STATE_KEY);
                String mComment = savedInstanceState.getString(COMMENT_STATE_KEY);

                // Set saved values
                activityDate.setText(mDate);
                activityTime.setText(mTime);
                activityDuration.setText(mDuration);
                activityDistance.setText(mDistance);
                activityCalorie.setText(mCalories);
                activityHeartbeat.setText(mHeartRate);
                activityCommentContent.setText(mComment);
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
                                    activityTime.setText(partyHour + ":" + partyMinute);
                                    _hour = partyHour;
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

            //You should be able to click either the comment or comment content to edit the comment
            activityCommentContent.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Stoppage in lifecycle, must save temp data
        String date = activityDate.getText().toString();
        String time = activityTime.getText().toString();
        String duration = activityDuration.getText().toString();
        String distance = activityDistance.getText().toString();
        String calories = activityCalorie.getText().toString();
        String heartbeat = activityHeartbeat.getText().toString();
        String comment = activityCommentContent.getText().toString();
        if(date != "" && date != null) {
            outState.putString(DATE_STATE_KEY, date);
        }
        if(time != "" && time != null) {
            outState.putString(TIME_STATE_KEY, time);
        }
        if(duration != "" && duration != null) {
            outState.putString(DURATION_STATE_KEY, duration);
        }
        if(distance != "" && distance != null) {
            outState.putString(DISTANCE_STATE_KEY, distance);
        }
        if(calories != "" && calories != null) {
            outState.putString(CALORIES_STATE_KEY, calories);
        }
        if(heartbeat != "" && heartbeat != null) {
            outState.putString(HEART_STATE_KEY, heartbeat);
        }
        if(comment != "" && comment != null) {
            outState.putString(COMMENT_STATE_KEY, comment);
        }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
                    //saveManualEntry();
                    task = new AsyncInsert();
                    task.execute();
                    Toast.makeText(getApplicationContext(),
                            "Saved",
                            Toast.LENGTH_SHORT).show();
                   // SystemClock.sleep(200);
                    finish();

                    //database save entry
                } else if(current_tab == 1){

                    delete_task = new AsyncDelete();
                    delete_task.execute();
                    //SystemClock.sleep(200);
                    finish();




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

    public void saveManualEntry() {
        mExercise = new Exercise();

        String time = _hour + ":" + _minute;
        String date = _year + "-" + _month + "-" + _day;
        String date_time = date + " " + time;
        String activity_name = mName.getText().toString();
        String duration = activityDuration.getText().toString();
        String distance = activityDistance.getText().toString();
        String calories = activityCalorie.getText().toString();
        String heartbeat = activityHeartbeat.getText().toString();
        String comment = activityCommentContent.getText().toString();

        int input = globs.getValue_int(globs.IN, "Manual");
        int activity = globs.getValue_int(globs.ACT, activity_name);

        mExercise.setmInputType(input);
        mExercise.setmActivityType(activity);
        mExercise.setmDateTime(date_time);
        if (globs.CURRENT_UNITS == 1 ){
            double miles = Double.parseDouble(MilesToKilometers(Double.parseDouble(distance)));
            Log.d("DEBUG", "IN MANUAL MILES  " + miles);
            mExercise.setmDistance(miles);
        } else {
            double kilo = Double.parseDouble(distance);
            mExercise.setmDistance(kilo);
            Log.d("DEBUG", "IN MANUAL KILOS   " + kilo);
        }

        mExercise.setmDuration(Integer.parseInt(duration));
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

    public String MilesToKilometers(double miles){
        double kilometer = 1.60934 * miles;
        String formatted = String.format("%.2f", kilometer);
        return formatted;
    }

    public String KilometersToMiles(double kilo){
        double miles = kilo * 0.621371;
        String formatted = String.format("%.2f", miles);
        return formatted;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
    }



    class AsyncInsert extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            saveManualEntry();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
               // ((MainActivity) context).onResult(result);
                //mAdapter.add(name[0]);
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
           Log.d(DEBUG_TAG, "INSERT THREAD DONE");
            task = null;
        }
    }

    class AsyncDelete extends AsyncTask<Void, String, Void> {
        int pos;
        @Override
        protected Void doInBackground(Void... unused) {
            Log.d("DEBUG", "USER HIT DELETE! and wants to Delete: " + id);
            Log.d("DEBUG", "USER HIT DELETE! and wants to Delete: "+ _id);

            String _pos = getIntent().getStringExtra(DELETE_ITEM);
            pos = Integer.parseInt(_pos);
            mEntry = new ExerciseEntry(getApplicationContext());
            mEntry.open();

            int ret = mEntry.deleteExercise(Long.valueOf(_id));
            mEntry.close();
            //ArrayList<Exercise> tempy = HistoryFragment.itemsData;

            if (ret>0){
                Log.d("DEBUG", "DeleteWorked and removed: " + _id);
                //ArrayList<Exercise> tempy = HistoryFragment.itemsData;
                //HistoryFragment.itemsData.remove(Long.valueOf(_id) + 1);
                // Thread broadcast = new ItemRemovedThread();
                //broadcast.run();
                //HistoryFragment.mAdapter.notifyDataSetChanged();

                //tempy.remove(Long.valueOf(_id) + 1);
                //HistoryAdapterRecycler adapt = HistoryFragment.mAdapter;
                // adapt.notifyDataSetChanged();
            } else {
                Log.d("DEBUG", "Delete Failed to remove: " + _id);
            }
            //mBusinessAdapter = new BusinessAdapter(mBusinesses);
            //adapt.notifyDataSetChanged();

            //mBusinessAdapter.notifyDataSetChanged();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
                //mAdapter.add(name[0]);
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(DEBUG_TAG, "Delete Done:   " + pos);
            task = null;
            Intent intent=new Intent();
            intent.putExtra(MainMyRunsActivity.MAIN_ITEM_TO_DELETE, String.valueOf(pos));
            setResult(1,intent);
            finish();//finishing activity

        }
    }



}
