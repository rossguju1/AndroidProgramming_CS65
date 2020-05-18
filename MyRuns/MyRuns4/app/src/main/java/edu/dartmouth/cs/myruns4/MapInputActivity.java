package edu.dartmouth.cs.myruns4;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.myruns4.database.ExerciseEntry;
import edu.dartmouth.cs.myruns4.fragments.StartFragment;
import edu.dartmouth.cs.myruns4.models.Exercise;
import edu.dartmouth.cs.myruns4.models.Constants;
import edu.dartmouth.cs.myruns4.models.MyGlobals;
import edu.dartmouth.cs.myruns4.services.TrackingService;

public class MapInputActivity extends AppCompatActivity implements OnMapReadyCallback, ServiceConnection {
    public static final String FROM = "from intent";
    public static final String DELETE_ITEM = "delete_item";
    public static final String DELETE_EXERCISE = "delete_exercise";
    private static final String DEBUG_TAG = "MapInputActivity";
    private static final String TAG = "MapInputActivity";
    public static final String FROM_MAPINPUT = "from_mapinput";
    private static final String INPUT_STATE_KEY = "input_state_key";
    private static final String FROM_STATE_KEY = "from_state_key";
    private static final String ROTATED_KEY = "rotated_key";
    private static final String AR_MAJORITY_KEY = "ar_majority_key";

    //MET Values for calorie calculation
    private static final float Running = (float) 12.5;
    private static final float Walking = (float) 3.5;
    private static final float Standing = (float) 1.2;
    private static final float Cycling = (float) 10;
    private static final float Hiking = (float) 6;
    private static final float DownhillSkiing = (float) 6;
    private static final float XCSkiing = (float) 8;
    private static final float Snowboarding = (float) 8;
    private static final float Skating = (float) 7;
    private static final float Swimming = (float) 10;
    private static final float MountainBiking = (float) 8.5;
    private static final float Wheelchair = (float) 8;
    private static final float Elliptical = (float) 5;

    private static DecimalFormat df = new DecimalFormat("0.00");
    private GoogleMap mMap;
    public Marker startMarker;
    public Marker finishMarker;
    public Polyline line;
    public List<Location> locations;
    private Exercise mExercise;

    private String mActivityName;
    private float mSpeed = 0;
    private float mAvgSpeed = 0;
    private float mClimbed = 0;
    private float mCalorie = 0;
    private float mDistance = 0;
    private List<LatLng> points;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Marker mMaker;
    private Intent serviceIntent;
    private int current_tab;
    private boolean deleteService = false;
    String coords = "";
    
    private String from_who;
    private  String which_input;

    private ServiceConnection mConnection = this; // as we implement ServiceConnection
    private Messenger mServiceMessenger = null;
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    boolean mIsBound;

    //Used for saving in DB
    private long id;
    private String _id;
    public MyGlobals globs;
    private SimpleDateFormat _sdf_date = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat _sdf_time = new SimpleDateFormat(" HH:mm");
    public Calendar cal = Calendar.getInstance();
    private MapInputActivity.AsyncInsert task = null;
    private MapInputActivity.AsyncDelete delete_task = null;
    private ExerciseEntry mEntry;
    private long startTime;
    private long prevTime;
    private float prevAltitude = -10000;
    private long start;
    private long finish;
    private long timeElapsed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate()");
        globs = new MyGlobals();
        //Create action bar with back button and name
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Map");
        setContentView(R.layout.activity_map_input);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        //globs = new MyGlobals();


        Intent intent = getIntent();
        from_who = getIntent().getStringExtra(FROM);//gets the name of who created this activity
        which_input = intent.getStringExtra(FROM_MAPINPUT);//gets auto or gps

        if (from_who == null){

            getInstanceInfo();
        } else {
            SharedPreferences mPrefs1 = getSharedPreferences("from_who", 0);
            SharedPreferences.Editor mEditor1 = mPrefs1.edit();
            mEditor1.putString("from_who", from_who).commit();
            Log.d(DEBUG_TAG, "Saving from_who: " + from_who);

            if (which_input == null){
                which_input = "";
            }
            SharedPreferences mPrefs2 = getSharedPreferences("which_input", 0);
            SharedPreferences.Editor mEditor2 = mPrefs2.edit();
            mEditor2.putString("which_input", which_input).commit();
            Log.d(DEBUG_TAG, "Saving which_input: " + which_input);

            if (which_input.equals("gps")){
                mActivityName = ((Intent) intent).getStringExtra("activity_name");

                Log.d(DEBUG_TAG, "Saving ActivityName:  " + mActivityName);

                SharedPreferences mPrefs3 = getSharedPreferences("mActivityName", 0);
                SharedPreferences.Editor mEditor3 = mPrefs3.edit();
                mEditor3.putString("mActivityName", mActivityName).commit();
            }


            start = System.currentTimeMillis();
            SharedPreferences mPrefs4 = getSharedPreferences("start", 0);
            SharedPreferences.Editor mEditor4 = mPrefs4.edit();
            mEditor4.putLong("start", start).commit();



            Log.d(DEBUG_TAG, "Saving start Time:  " + start);

        }

        if(from_who != null && from_who.equals("start_tab")) {
            Log.d(DEBUG_TAG, "which input1:  " + which_input);

            if (which_input.equals("auto")) {
                Log.d(DEBUG_TAG, "which input2:  " + which_input);
                globs.initAR_majority();
                setActivityText("Unknown");
                // globs.initAR_majority();

            } else{
                //mActivityName = ((Intent) intent).getStringExtra("activity_name");
                Log.d(DEBUG_TAG, "mActivity Name " + mActivityName);
                setActivityText(mActivityName);
            }

            setAvgSpeedText(mAvgSpeed);
            Log.d("ATTEMPTING SET HERE ***","HERE!!!!");
            setCurSpeedText(mSpeed);
            setCalorieText(mCalorie);
            setElevationDifText(mClimbed);
            setDistanceText(mDistance);


        } else if(from_who.equals("history_tab")) {
            current_tab = 1;
            _id = intent.getStringExtra(DELETE_EXERCISE);
            id = Long.parseLong(_id);
            Log.d("DEBUG", "INSIDE MANUAL FROM *HISTORY* Tab and clicked on ID: " + id );

            mEntry = new ExerciseEntry(this);
            mEntry.open();

            Exercise e = mEntry.fetchEntryByIndex(id);
            setActivityText(globs.getValue_str(globs.ACT, e.getmActivityType()));
            if (globs.CURRENT_UNITS == 1) {
                setDistanceText(Float.parseFloat(KilometersToMiles(e.getmDistance())));
            } else {
                setDistanceText((float)e.getmDistance());
            }
            setCalorieText((float)e.getmCalories());
            setCurSpeedText((float)e.getmSpeed());
            setAvgSpeedText((float)e.getmAvgSpeed());
            setElevationDifText((float)e.getmClimb());
            setDistanceText((float)e.getmDistance());

            //This fills our points ArrayList
            parseMapData(e.getmLocationList());
            Log.d(DEBUG_TAG, "list of mLocation Points:  " + e.getmLocationList());

            //Now update our map from the saved data
            mEntry.close();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (from_who != null && which_input != null ){
            outState.putString(INPUT_STATE_KEY, which_input);
            outState.putString(FROM_STATE_KEY, from_who);
        }
    }

    // save data across orientation changes.
    // comment them out and see what will happen (textviews will disappear and appear)

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            which_input = savedInstanceState.getString(INPUT_STATE_KEY);
            from_who = savedInstanceState.getString(FROM_STATE_KEY);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart():start Tracking Service");
	try {
	    if(!TrackingService.isRunning()) {
            Log.d(TAG, "onStart(): Tracking service not yet running");
            //We only want to kick off broadcasting logic if we are starting a new gps entry
            if (from_who != null && from_who.equals("start_tab")) {
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mActivityBroadcastReceiver,
                        new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocationBroadcastReceiver,
                        new IntentFilter(Constants.BROADCAST_DETECTED_LOCATION));
                startTrackingService();

            }
        } else {

            Log.d(TAG, "onStart(): Tracking service is already Running");
            if (TrackingService.isPaused && TrackingService.isRunning()){
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocationBroadcastReceiverString,
                        new IntentFilter(Constants.BROADCAST_DETECTED_LOCATION_STRING));
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocationBroadcastReceiver,
                        new IntentFilter(Constants.BROADCAST_DETECTED_LOCATION));
                if (which_input.equals("auto")){
                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mActivityBroadcastReceiver,
                            new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mActivityBroadcastReceiverString,
                            new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY_STRING));
                }

                startTrackingService();

            }


        }
	} catch (Exception e){
	    Log.d(DEBUG_TAG, "onStart() Exception ");
	}
    }

    BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_LOCATION)) {

                Location location = intent.getParcelableExtra("location");

                Log.d(TAG, "onReceive() Locations " + location.getLongitude() + location.getLatitude());
                if (coords.equals("")){
                    coords = coords + location.getLongitude() + "," + location.getLatitude();
                } else {
                    coords = coords + "|"  + location.getLongitude() + "," + location.getLatitude();
                }

                Log.d(TAG, "cumalative Locations: " + coords);

                upDateMap(location);
            }
        }
    };

    BroadcastReceiver mActivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "onReceive()");
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                if (globs != null) {
                    if (confidence > 70) {
                        try {
                            setActivityText(globs.getValue_str(globs.ACT, convertUserActivity(type)));
                            globs.setAR_majority(type);
                        } catch (Exception e){

                            Log.d(DEBUG_TAG, "Failed to update AR");
                        }

                    }
                }
                Log.d(TAG, "onReceive() AR " + "Type: " + type + "confidence: " + confidence);

            }
        }
    };
    BroadcastReceiver  mLocationBroadcastReceiverString =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "onReceive()");
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_LOCATION_STRING)) {
                String locs = intent.getStringExtra("location_strings");



                Log.d(DEBUG_TAG, "onReceive() Locations list (String))   " + locs);

            }
        }
    };
    BroadcastReceiver mActivityBroadcastReceiverString =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "onReceive()");
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY_STRING)) {
                String activity_list = intent.getStringExtra("activity_strings");



                Log.d(DEBUG_TAG, "onReceive() Activity list (String))   " + activity_list);

            }
        }
    };
    private int convertUserActivity(int type){
        int label = -1;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = 13;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = 3;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = 1;
                break;
            }
            case DetectedActivity.RUNNING: {
                label = 0;
                break;
            }
            case DetectedActivity.STILL: {
                label = 2;
                break;
            }
            case DetectedActivity.TILTING: {
                label = 13;
                break;
            }
            case DetectedActivity.WALKING: {
                label = 1;
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = 13;
                break;
            }

        }

        return label;
    }
    private String handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "In Vehicle";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "On Bicycle";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "On Foot";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "Running";
                break;
            }
            case DetectedActivity.STILL: {
                label = "Still";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Tilting";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "Walking";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                break;
            }

        }
        return label;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume");




    }

    public void getInstanceInfo(){

        SharedPreferences mPrefs1 = getSharedPreferences("from_who", 0);
        from_who = mPrefs1.getString("from_who", "");


        SharedPreferences mPrefs2 = getSharedPreferences("which_input", 0);
        which_input = mPrefs2.getString("which_input", "");

        if (which_input.equals("gps")) {
            SharedPreferences mPrefs3 = getSharedPreferences("mActivityName", 0);
            mActivityName = mPrefs3.getString("mActivityName", "");
            Log.d(DEBUG_TAG, "getInstanceInfo(): " + mActivityName);
        }

        SharedPreferences mPrefs4 = getSharedPreferences("start", 0);
        start = mPrefs4.getLong("start", 0);


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause");

        /*

        if(from_who.equals("start_tab")) {
            sendMessageToService(Constants.MSG_PAUSE);
            if (mLocationBroadcastReceiver != null) {
                // stopService(new Intent(this, TrackingService.class));
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReceiver);
            }
            if (mActivityBroadcastReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityBroadcastReceiver);
            }
            doUnbindService();
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocationBroadcastReceiverString,
                    new IntentFilter(Constants.BROADCAST_DETECTED_LOCATION_STRING));
            if (which_input.equals("auto")) {
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocationBroadcastReceiverString,
                        new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY_STRING));
            }

        }

         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");

    }

    private void destroy() {
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }

        if (mLocationBroadcastReceiver != null) {
            // stopService(new Intent(this, TrackingService.class));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReceiver);
        }
        if (mActivityBroadcastReceiver != null) {
            // stopService(new Intent(this, TrackingService.class));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityBroadcastReceiver);
        }
        stopService(new Intent(this, TrackingService.class));
    }
    
    private boolean saveMapData() {
        //NOTE: I'm going to need to make the first 6 sections of locationString data about exercise
        // - Activity Type
        // - Speed
        // - Avg Speed
        // - Altitude Gain
        // - Calories
        // - Distance
        mExercise = new Exercise();
        //Save the activity on the map data return true if successful
        if(locations != null){
            String exerciseString = "";
            for(Location location : locations) {
                exerciseString =
                        exerciseString +
                        location.getLatitude() + "," +
                        location.getLongitude() + "@";
            }
            Log.d(DEBUG_TAG, "SAVING MAP DATA:  "+ exerciseString);
            if(which_input.equals("gps")){
                int input = globs.getValue_int(globs.IN, "GPS");
                mExercise.setmInputType(input);
                int activity = globs.getValue_int(globs.ACT, mActivityName);
                mExercise.setmActivityType(activity);
            } else {
                int input = globs.getValue_int(globs.IN, "Automatic");
                mExercise.setmInputType(input);
                int _activity = globs.getAR_majorityMAJORITY();

                Log.d(DEBUG_TAG, "inserting into DB AR MAJORITY number before convert  " +_activity );


                int MajorityActivity = convertUserActivity(_activity);
                Log.d(DEBUG_TAG, "inserting into DB AR MAJORITY number after convert  " + MajorityActivity);


                Log.d(DEBUG_TAG, "inserting into DB AR MAJORITY NAME:  " + globs.getValue_str(globs.ACT, MajorityActivity));
                mExercise.setmActivityType(MajorityActivity);


            }


            String time = _sdf_time.format(cal.getTime());
            String date = _sdf_date.format(cal.getTime());
            String date_time = date + " " + time;
            mExercise.setmLocationList(exerciseString);


            mExercise.setmDateTime(date_time);
            Log.d(DEBUG_TAG, "SAVED: DATE/TIME: "+date_time);
            mExercise.setmAvgSpeed(mAvgSpeed);
            Log.d(DEBUG_TAG, "SAVED: Average Speed: "+ mAvgSpeed);
            mExercise.setmSpeed(mSpeed);
            Log.d(DEBUG_TAG, "SAVED: speed: " + mSpeed);
            finish = System.currentTimeMillis();
            long _timeElapsed = finish - start;
            String temp = String.valueOf(_timeElapsed);
            int exerTime = (Integer.parseInt(temp) / 60000);
            Log.d(DEBUG_TAG, "SAVED: TIME ELAPSED: " + exerTime);
            mExercise.setmDuration(exerTime);
            mEntry =  new ExerciseEntry(this);
            mEntry.open();
            id = mEntry.insertEntry(mExercise);
            mEntry.close();
            return true;
        }
        return false;
    }

    private void parseMapData(String data) {
        points = new ArrayList<LatLng>();
        String[] LatLongPairs = data.split("@");
        int counter = 0;
        String[] pair;
        for(String LatLngPair : LatLongPairs) {
            pair = LatLngPair.split(",");
            points.add(new LatLng(Double.valueOf(pair[0]),Double.valueOf(pair[1])));
        }
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

    public float KPHtoMPH(float kilometer){
        float mph = (float) (kilometer / 1.609);
        return mph;
    }

    public float MtoFt(float meters){
        return (float) (meters/(3.281));
    }

    private LatLng fromLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void updateWithNewLocation(Location location) {
        String latLongString = "No location found";
        String addressString = "No address found";
        if (location != null) {
            if(locations == null){
                locations = new ArrayList<>();
            }
            locations.add(location);
            // Update the map location.
            LatLng latlng = fromLocationToLatLng(location);

            //Only want to add our start marker the first time
            if (startMarker == null) {
                startMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)).title("Start"));
            }

            //Remove old and Update the finish marker to the lat/long point each time
            if (finishMarker != null) {
                finishMarker.remove();
            }
            finishMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED)).title("Finish"));

            // If our points array hasn't been created, create it and add our first point
            // If our polyline hasn't been created add our second point to points and create the line
            // If we have our points and our line then add another point and update the line
            if(points == null){
                points = new ArrayList<>();
                points.add(latlng);
            } else if(line == null) {
                points.add(latlng);
                line = mMap.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .width(5)
                        .color(Color.BLUE));
            } else {
                LatLng prev = points.get(points.size() -1);
                float[] dist = {(float)0};
                Location.distanceBetween(latlng.latitude, latlng.longitude,prev.latitude,prev.longitude, dist);
                mDistance = mDistance + (float)dist[dist.length -1];
                setDistanceText(mDistance);
                points.add(latlng);
                line.setPoints(points);

                //Calculate our speed and avgspeed
                if(prevTime != 0) {
                    long curTime = System.currentTimeMillis();
                    long timeElapsed = curTime - prevTime;
                    long totalTimeElapsed = curTime - startTime;
                    mSpeed = (mDistance * 1000) / ((float)timeElapsed / (float)3600000);
                    mAvgSpeed = (mDistance * 1000) / ((float)totalTimeElapsed / (float)3600000);
                    prevTime = curTime;

                    //Also want to update calories using time elapsed

                    //Calc calories provides estimate for calories per hour, multiply by duration in hours
                    //for the total calories burned
                    mCalorie = (calcCalories(getMetValue(mActivityName)) * ((float)totalTimeElapsed / (float)3600000));
                    setCalorieText(mCalorie);
                } else {
                    prevTime = System.currentTimeMillis();
                    startTime = System.currentTimeMillis();
                }
                //Set text with appropriate units

                setCurSpeedText(mSpeed);
                setAvgSpeedText(mAvgSpeed);

                //if we have a prev alt check diff and calculated climb
                if(prevAltitude != -10000){
                    float curAlt = (float) location.getAltitude();
                    if(prevAltitude < curAlt){
                        mClimbed = mClimbed + (curAlt - prevAltitude);
                    }
                }else {
                    prevAltitude = (float) location.getAltitude();
                }
                setElevationDifText(mClimbed);
            }
        }
    }

    private void setMapFromSave(List<LatLng> points) {
        startMarker = mMap.addMarker(new MarkerOptions().position(points.get(0)).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)).title("Start"));
        finishMarker = mMap.addMarker(new MarkerOptions().position(points.get(points.size() -1)).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED)).title("Finish"));

        line = mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(5)
                .color(Color.BLUE));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size() -1), 17));

    }

    private void startTrackingService() {





        serviceIntent = new Intent(getApplicationContext(), TrackingService.class);
        serviceIntent.setAction("start");

        mIsBound = false; // by default set this to unbound
        automaticBind(serviceIntent);

    }
    private void automaticBind(Intent intent) {
            Log.d(TAG, "C:MyService.isRunning: doBindService()");
            doBindService(intent);

    }

    private void doBindService(Intent intent) {
        Log.d(TAG, "C:doBindService()");

        // pass mConnection to tell the server it is this activity that is trying to bind to the server.
        //
        // For bindService(Intent, ServiceConnection, flag) if flag Context.BIND_AUTO_CREATE is used
        // it will bind the service and start the service, but if "0" is used, method will return true and
        // will not start service until a call like startService(Intent) is made to start the service.
        // One of the common use of "0" is in the case where an activity to connect to a local service if that
        // service is running, otherwise you can start the service.

       // bindService(intent, mConnection, 0);// http://stackoverflow.com/questions/14746245/use-0-or-bind-auto-create-for-bindservices-flag
        mIsBound = true;


        if(TrackingService.isRunning()){
            Log.d(DEBUG_TAG, "Tracking Service is still Running!");
            getApplicationContext().bindService(intent, mConnection, 0);
        }else {
            getApplicationContext().bindService(intent, mConnection, 0);
            startForegroundService(intent);
        }
    }


    private void doUnbindService() {
        Log.d(TAG, "C:doUnBindService()");
        if (mIsBound) {
            // If we have received the service, and hence registered with it,
            // then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, Constants.MSG_UNREGISTER_CLIENT);//  obtain (Handler h, int what) - 'what' is the tag of the message, which will be used in line 72 in MyService.java. Returns a new Message from the global message pool. More efficient than creating and allocating new instances.


                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);// need to use the server messenger to send the message to the server
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has
                    // crashed.
                }
            }
            // Detach our existing connection.
            getApplicationContext().unbindService(mConnection);
            Log.d(TAG, "C:doUnBindService() Actually unbinded");
            mIsBound = false;
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "C:onServiceConnected()");
        // this is the Messenger defined in line 49 of MyService.java
        mServiceMessenger = new Messenger(service);
        try {
            if(which_input.equals("auto")) {
                Message msg = Message.obtain(null, Constants.MSG_REGISTER_CLIENT, Constants.MSG_AUTO, Constants.MSG_START_FRAGMENT);
                msg.replyTo = mMessenger;
                Log.d(TAG, "C: TX MSG_REGISTER_CLIENT: AUTO");

                // We use service Messenger to send the msg to the Server
                mServiceMessenger.send(msg);
            } else if(which_input.equals("gps")) {
                Message msg = Message.obtain(null, Constants.MSG_REGISTER_CLIENT, Constants.MSG_GPS, Constants.MSG_START_FRAGMENT);
                msg.replyTo = mMessenger;
                Log.d(TAG, "C: TX MSG_REGISTER_CLIENT: GPS");

                // We use service Messenger to send the msg to the Server
                mServiceMessenger.send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }


    /**
     * Send data to the service
     *
     * @param intvaluetosend The data to send
     */
    private void sendMessageToService(int intvaluetosend) {
        if (mIsBound) {
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, Constants.MSG_SET_INT_VALUE, intvaluetosend, 0);// http://developer.android.com/intl/es/reference/android/os/Message.html#obtain()
                    msg.replyTo = mMessenger;
                    // we use the server messenger to send msg to the server
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "C:IncomingHandler:handleMessage " + msg.replyTo);
            switch (msg.what) {
                case Constants.MSG_SET_INT_VALUE:
                    Log.d(TAG, "C: RX MSG_SET_INT_VALUE");
                    // msg.arg1 here as only arg1 was used to store data in the server class.
                    Log.d(DEBUG_TAG, "Tracking Service echos:  "+  msg.arg1);
                    if (msg.arg1 == Constants.MSG_GPS ){

                        which_input = "gps";
                    }
                    if( msg.arg1 == Constants.MSG_AUTO){
                        which_input = "auto";

                    }
                    if (msg.arg1 == Constants.MSG_HISTORYFRAGMENT){
                        from_who = "history_fragment";

                    }
                    if (msg.arg1 == Constants.MSG_START_FRAGMENT){
                        from_who = "start_fragment";
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    // This is called when the connection with the service has been
    // established, giving us the service object we can use to
    // interact with the service.

    // bindService(new Intent(this, MyService.class), mConnection,
    // Context.BIND_AUTO_CREATE) calls onbind in the service which
    // returns an IBinder to the client.

    // this class implements ServiceConnection so onServiceConnected needs to be implemented.
    // onServiceConnected() is called when binding to the server is successful.

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "C:onServiceDisconnected()");
        // This is called when the connection with the service has been
        // unexpectedly disconnected - process crashed.
        mServiceMessenger = null;

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
        if (!checkPermission()){
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}
                    , PERMISSION_REQUEST_CODE);
        }
        else{
            if(from_who.equals("start_tab")) {
                upDateMap(null);
            } else if(from_who.equals("history_tab")){
                setMapFromSave(points);
            }
        }
    }

    private void upDateMap(Location loc) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //NOT SURE IF I NEED THIS
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (loc == null) {
            Log.d(DEBUG_TAG, "First time map is being made. Manually get location");
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            // Note that fine as provider may return null object
            // write defensive code that determines what might be the best provider
            // to give the best last location see getLastKnownLocation() here
            // https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
            //criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(true);
            criteria.setCostAllowed(true);
            String provider = locationManager.getBestProvider(criteria, true);


            Location l = locationManager.getLastKnownLocation(provider);

            LatLng latlng = fromLocationToLatLng(l);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Update our camera to our current location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17)); //17: the desired zoom level, in the range of 2.0 to 21.0
            updateWithNewLocation(l);
        } else {

            LatLng latlng = fromLocationToLatLng(loc);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Update our camera to our current location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17)); //17: the desired zoom level, in the range of 2.0 to 21.0
            updateWithNewLocation(loc);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission granted. Let's show the map");
            upDateMap(null);
        } else {
            Log.d(TAG, "permission denied! I am going to close the app");
            finish();
        }
    }

    //******** Check run time permission for locationManager. This is for v23+  ********
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d("****** RESULT OF CHECK SELF PERMISSION: ", "RESULT: " + result);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }


    public float getMetValue(String activity){
        if(activity != null) {
            switch (activity) {
                case "Running":
                    return Running;
                case "Walking":
                    return Walking;
                case "Standing":
                    return Standing;
                case "Cycling":
                    return Cycling;
                case "Hiking":
                    return Hiking;
                case "Downhill Skiing":
                    return DownhillSkiing;
                case "Cross-Country Skiing":
                    return XCSkiing;
                case "Snowboarding":
                    return Snowboarding;
                case "Skating":
                    return Skating;
                case "Swimming":
                    return Swimming;
                case "Mountain Biking":
                    return MountainBiking;
                case "Wheelchair":
                    return Wheelchair;
                case "Elliptical":
                    return Elliptical;
                default:
                    return (float) 0.0;
            }
        }else {
            return (float) 0.0;
        }
    }

    //Calculate our calories burned for each activity
    public float calcCalories(float MET){
        // (METs x 3.5 x (your body weight in kilograms) / 200) * 60 = calories burned per hour
        // https://www.healthline.com/health/what-are-mets#calorie-connection
        // Assume avg weight of 80kg
        // Assume moderate effort
        // MET values by Activity:
        // https://community.plu.edu/~chasega/met.html

        return (float) ((MET * 3.5 * 80 / 200)*60);
    }


    // Setters for our map overlay text
    public void setActivityText(String activityName) {
        TextView activity = (TextView) findViewById(R.id.activity_name);
        if(activityName != null){
            activity.setText("Activity: " + activityName);
        }
    }

    public void setCurSpeedText(float speed) {
        Log.d("BEFORE UNITS GET HERE ********", "HERE");
        TextView activity = (TextView) findViewById(R.id.cur_speed);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String units = sharedPreferences.getString("list_preference", "");
        if(units.equals("kms")) {
            activity.setText("Speed: " + df.format(speed/360000) + " m/s");
        } else if (units.equals("mi")) {
            activity.setText("Speed: " + df.format(KPHtoMPH(speed)/360000) + " mph");
        }
    }

    public void setAvgSpeedText(float speed) {
        TextView activity = (TextView) findViewById(R.id.avg_speed);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String units = sharedPreferences.getString("list_preference", "");

        if(units.equals("kms")){
            activity.setText("Avg Speed: " + df.format(speed/360000) + " m/s");
        } else if (units.equals("mi")) {
            activity.setText("Avg Speed: " + df.format(KPHtoMPH(speed)/360000) + " mph");
        }
    }

    public void setElevationDifText(float elevation) {
        TextView activity = (TextView) findViewById(R.id.elevation_dif);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String units = sharedPreferences.getString("list_preference", "");
        if(units.equals("kms")){
            activity.setText("Climbed: " + df.format(elevation) + " m");
        } else if (units.equals("mi")) {
            activity.setText("Climbed: " + df.format(elevation) + " ft");
        }
    }

    public void setCalorieText(float calorie) {
        TextView activity = (TextView) findViewById(R.id.calorie);
        activity.setText("Calorie: " + df.format(calorie) + " cal");
    }

    public void setDistanceText(float distance) {
        TextView activity = (TextView) findViewById(R.id.distance);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String units = sharedPreferences.getString("list_preference", "");

        if(units.equals("kms")){
            activity.setText("Distance: " + df.format(distance) + " m");
        } else if (units.equals("mi")) {
            activity.setText("Distance: " + df.format(MtoFt(distance)) + " ft");
        }

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
                destroy();
                finish();
                return true;
            case R.id.map_save:
                Log.d("mAPPP SAVE WAS CLICKED ", "-------------");
                if (current_tab == 0) {
                    task = new MapInputActivity.AsyncInsert();
                    task.execute();
                    Toast.makeText(getApplicationContext(),
                            "Saved",
                            Toast.LENGTH_SHORT).show();
                } else if(current_tab == 1){
                    Log.d("CURRENT TAB ! SHOULD BE DELETE", "-------------");
                    delete_task = new MapInputActivity.AsyncDelete();
                    delete_task.execute();
                    Toast.makeText(getApplicationContext(),
                            "Delete",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        Log.d(DEBUG_TAG, "onNewIntent()");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("INSIDE ON CREATE OPTIONS MENU: " + from_who,"  ********");
        getMenuInflater().inflate(R.menu.map_activity_menu, menu);
        //Set the appropriate button title depending on navigation context
        if(from_who.equals("start_tab")){
            current_tab = 0;
            menu.getItem(0).setTitle("SAVE");
        }else if (from_who.equals("history_tab")){
            current_tab = 1;
            menu.getItem(0).setTitle("DELETE");
        }
        return super.onCreateOptionsMenu(menu);
    }

    class AsyncInsert extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            saveMapData();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {

            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(DEBUG_TAG, "INSERT THREAD DONE");
            task = null;
            Intent intent=new Intent();
            Log.d(DEBUG_TAG, "INSERT THREAD  passing ID  " + id );
            intent.putExtra(StartFragment.START_INSERT_ITEM, String.valueOf(id));

            setResult(2, intent);


            if (mLocationBroadcastReceiver != null) {
                // stopService(new Intent(this, TrackingService.class));
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLocationBroadcastReceiver);
            }
            if (mActivityBroadcastReceiver != null) {
                // stopService(new Intent(this, TrackingService.class));
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mActivityBroadcastReceiver);
            }

            doUnbindService();


            stopService(new Intent(getApplicationContext(), TrackingService.class));

            finish();




        }
    }
    class AsyncDelete extends AsyncTask<Void, String, Void> {
        int pos;

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d("DEBUG", "USER HIT DELETE! and wants to Delete: " + id);
            Log.d("DEBUG", "USER HIT DELETE! and wants to Delete: " + _id);

            String _pos = getIntent().getStringExtra(DELETE_ITEM);
            pos = Integer.parseInt(_pos);
            mEntry = new ExerciseEntry(getApplicationContext());
            mEntry.open();

            int ret = mEntry.deleteExercise(Long.valueOf(_id));
            mEntry.close();

            if (ret > 0) {
                Log.d("DEBUG", "DeleteWorked and removed: " + _id);

            } else {
                Log.d("DEBUG", "Delete Failed to remove: " + _id);
            }

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
            Intent intent = new Intent();
            intent.putExtra(MainMyRunsActivity.MAIN_ITEM_TO_DELETE, String.valueOf(pos));
            setResult(1, intent);
            finish();





        }
    }
}
