package edu.dartmouth.cs.myruns2.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;

import edu.dartmouth.cs.myruns2.models.Constants;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationService extends  IntentService {
    protected static final String TAG = TrackingService.class.getSimpleName();

    private static final long UPDATE_INTERVAL = 5000;
    private static final long FAST_INTERVAL = 1000;
    public static final String BROADCAST_LOCATION = "location update";

    public LocationService() {
        super(TAG);
        // Log.d(TAG,TAG + "DetectedActivityIntentService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Log.d(TAG,TAG + "onCreate()");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,TAG + "onHandleIntent()");
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.

        List<DetectedActivity> detectedActivities = result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            //Log.d(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            broadcastActivity(activity);
        }
    }


    private void startLocationUpdates() {

        // set criteria
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);

        // check if the system can support the criteria

        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback()  {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "TrackingService: onLocationResult(): Thread ID is:" + Thread.currentThread().getId());
            Intent intent = new Intent(BROADCAST_LOCATION);
            intent.putExtra("location", locationResult.getLastLocation());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }


        private void broadcastActivity(DetectedActivity activity) {
        // Log.d(TAG,TAG+ "broadcastActivity()");
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_LOCATION);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
