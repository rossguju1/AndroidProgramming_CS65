package edu.dartmouth.cs.myruns2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.soundcloud.android.crop.Crop;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import edu.dartmouth.cs.myruns2.database.ExerciseEntry;
import edu.dartmouth.cs.myruns2.fragments.StartFragment;
import edu.dartmouth.cs.myruns2.models.Exercise;
import edu.dartmouth.cs.myruns2.models.Constants;
import edu.dartmouth.cs.myruns2.models.MyGlobals;
import edu.dartmouth.cs.myruns2.services.LocationService;
import edu.dartmouth.cs.myruns2.services.TrackingService;

public class MapInputActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String FROM = "from intent";
    public static final String DELETE_ITEM = "delete_item";
    public static final String DELETE_EXERCISE = "delete_exercise";
    private static final String DEBUG_TAG = "MapInputActivity";
    private static final String TAG = "MapsActivity";
    public static final String FROM_MAPINPUT = "from_mapinput";
    private GoogleMap mMap;
    public Marker startMarker;
    public Marker finishMarker;
    public Polyline line;
    public List<Location> locations;
    private Exercise mExercise;

    private String mActivityName;
    private float mSpeed;
    private ArrayList<Float> speedCollection;
    private float mAvgSpeed;
    private String mClimbed;
    private String mCalorie;
    private String mDistance;
    private List<LatLng> points;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Marker mMaker;
    private Intent serviceIntent;
    private int current_tab;
    String coords = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create action bar with back button and name
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Map");
        setContentView(R.layout.activity_map_input);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        globs = new MyGlobals();

        // TODO check which intent created this activity
        //  If start tab was the creating intent do the stuff now (i.e. getIntent()....)
        //  If this was made by the history fragment (i.e. Recycler adapter)
        //  Get the exercise and show the info in the corner of the map
        //  and read the coordinates in the db and show them on the screen


        Intent intent = getIntent();
        if(getIntent().getStringExtra(FROM) != null && getIntent().getStringExtra(FROM).equals("start_tab")) {
            mActivityName = ((Intent) intent).getStringExtra("activity_name");
            setActivityText(mActivityName);
            setCalorieText("0");
            setElevationDifText("0");
            setDistanceText("0");
        } else {
            current_tab = 1;
            _id = getIntent().getStringExtra(DELETE_EXERCISE);
            id = Long.parseLong(_id);
            Log.d("DEBUG", "INSIDE MANUAL FROM *HISTORY* Tab and clicked on ID: " + id );

            mEntry = new ExerciseEntry(this);
            mEntry.open();

            Exercise e = mEntry.fetchEntryByIndex(id);
            setActivityText(globs.getValue_str(globs.ACT, e.getmActivityType()));
            if (globs.CURRENT_UNITS == 1) {
                setDistanceText(String.valueOf(KilometersToMiles(e.getmDistance())));
            } else {
                setDistanceText(String.valueOf(e.getmDistance()));
            }
            setCalorieText(String.valueOf(e.getmCalories()));
            setCurSpeedText((float)e.getmSpeed());
            setAvgSpeedText((float)e.getmAvgSpeed());
            //This fills our points ArrayList
            parseMapData(e.getmLocationList());
            //Now update our map from the saved data
            mEntry.close();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
        Log.d(TAG, "onStart():start Tracking Service");

        //We only want to kick off broadcasting logic if we are starting a new gps entry
        if(getIntent().getStringExtra(FROM) != null && getIntent().getStringExtra(FROM).equals("start_tab")) {

            LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_DETECTED_LOCATION));


            LocalBroadcastManager.getInstance(this).registerReceiver(mActivityBroadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));


            startTrackingService();
        }
//        if(getIntent().getStringExtra(FROM).equals("history_tab")) {
//
//        }
    }

    BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "onReceive()");
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_LOCATION)) {


                Location location = intent.getParcelableExtra("location");
                Log.d(TAG, "onReceive() Locations " + location.getLongitude() + location.getLatitude());
                if (coords.equals("")){
                    coords = coords + location.getLongitude() + "," + location.getLatitude();
                } else {
                    coords = coords + "|"  + location.getLongitude() + "," + location.getLatitude();
                }

                Log.d(TAG, "cumalative Locations: " + coords);

                // TODO
                //  now create Async Task that updates the rest of the db entries
                //  The async task not only updates the coordinates
                //  but also updates calories, distance, duration, ect
                //
                //
                //  TODO Then update the map
                upDateMap();
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
                Log.d(TAG, "onReceive() AR " + "Type: " + type + "confidence: " + confidence);
                Toast.makeText(getApplicationContext(), "Auto Detected:  " +  handleUserActivity(type, confidence), Toast.LENGTH_SHORT).show();

                //handleUserActivity(type, confidence);

                // TODO psuedo code for  AsyncTask auto_insert()
                //  if confidence > 70: update activity name
                //  update the other parameters
                //
                //  TODO Then update the info in the corner of the screen
            }
        }
    };
    private String handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "In_Vehicle";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "On_Bicycle";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "On_Foot";
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
        return label + " with " + confidence;
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
        if(getIntent().getStringExtra(FROM).equals("start_tab")) {
            if(mLocationBroadcastReceiver!= null){
                stopService(new Intent(this, TrackingService.class));
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReceiver);
            }

            if(mActivityBroadcastReceiver != null){
                stopService(new Intent(this,TrackingService.class));
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityBroadcastReceiver);
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
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

            int input = globs.getValue_int(globs.IN, "GPS");
            int activity = globs.getValue_int(globs.ACT, mActivityName);
            String time = _sdf_time.format(cal.getTime());
            String date = _sdf_date.format(cal.getTime());
            String date_time = date + " " + time;
            mExercise.setmLocationList(exerciseString);
            mExercise.setmInputType(input);
            mExercise.setmActivityType(activity);
            mExercise.setmDateTime(date_time);
            mExercise.setmAvgSpeed(mAvgSpeed);
            mExercise.setmSpeed(mSpeed);
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
        Log.d("Data HERE *********",data);
        String[] LatLongPairs = data.split("@");
        int counter = 0;
        String[] pair;
        for(String LatLngPair : LatLongPairs) {
            pair = LatLngPair.split(",");
            Log.d("PAIR *************",pair[0]);
            Log.d("PAIR *************",pair[1]);
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

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
                points.add(latlng);
                line.setPoints(points);
            }

            //Calculate our speed and avgspeed
            mSpeed = location.getSpeed();
            if(speedCollection != null){
                speedCollection.add(mSpeed);
            }else {
                speedCollection = new ArrayList<Float>();
                speedCollection.add(mSpeed);
            }

            setCurSpeedText(mSpeed);
            mAvgSpeed = calculateAvgSpeed(speedCollection);
            setAvgSpeedText(mAvgSpeed);
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
        serviceIntent = new Intent(this, TrackingService.class);
        if (getIntent().getStringExtra(FROM_MAPINPUT).equals("auto")){
            serviceIntent.putExtra(TrackingService.TRACKING_TYPE, "auto");

        }else{
            serviceIntent.putExtra(TrackingService.TRACKING_TYPE, "gps");
        }
        startForegroundService(serviceIntent);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }

        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }
    };

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
            if(getIntent().getStringExtra(FROM).equals("start_tab")) {
                upDateMap();
            } else if(getIntent().getStringExtra(FROM).equals("history_tab")){
                setMapFromSave(points);
            }
        }
    }

    private void upDateMap() {
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
        Location l = locationManager.getLastKnownLocation(provider);

        LatLng latlng = fromLocationToLatLng(l);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Update our camera to our current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17)); //17: the desired zoom level, in the range of 2.0 to 21.0
        updateWithNewLocation(l);
        // update once every 2 second, min distance 0 therefore not considered
        locationManager.requestLocationUpdates(provider, 2000, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission granted. Let's show the map");
            upDateMap();
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
    //****** Check run time permission ************

    // Setters for our map overlay text
    public void setActivityText(String activityName) {
        TextView activity = (TextView) findViewById(R.id.activity_name);
        if(activityName != null){
            activity.setText("Activity: " + activityName);
        }
    }

    public void setCurSpeedText(Float speed) {
        TextView activity = (TextView) findViewById(R.id.cur_speed);
        if(speed != null){
            activity.setText("Speed: " + speed + " m/s");
        }
    }

    public float calculateAvgSpeed(ArrayList<Float> speeds) {
        float sum = 0;
        if(speeds != null){
            for(float speed : speeds){
                sum = sum + speed;
            }
            return sum/speeds.size();
        }
        return (float)0;
    }

    public void setAvgSpeedText(float speed) {
        TextView activity = (TextView) findViewById(R.id.avg_speed);
        activity.setText("Activity: " + speed + " m/s");
    }

    public void setElevationDifText(String elevation) {
        TextView activity = (TextView) findViewById(R.id.elevation_dif);
        if(elevation != null){
            activity.setText("Climbed: " + elevation + " m");
        }
    }

    public void setCalorieText(String calorie) {
        TextView activity = (TextView) findViewById(R.id.calorie);
        if(calorie != null){
            activity.setText("Calorie: " + calorie + " cal");
        }
    }

    public void setDistanceText(String distance) {
        TextView activity = (TextView) findViewById(R.id.distance);
        if(distance != null){
            activity.setText("Distance: " + distance + " m");
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
                return true;
            case R.id.map_save:
                if (current_tab == 0) {
                    task = new MapInputActivity.AsyncInsert();
                    task.execute();
                    Toast.makeText(getApplicationContext(),
                            "Saved",
                            Toast.LENGTH_SHORT).show();
                } else if(current_tab == 1){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_activity_menu, menu);
        //Set the appropriate button title depending on navigation context
        if(getIntent().getStringExtra(FROM).equals("start_tab")){
            current_tab = 0;
            menu.getItem(0).setTitle("SAVE");
        }else if (getIntent().getStringExtra(FROM).equals("history_tab")){
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
