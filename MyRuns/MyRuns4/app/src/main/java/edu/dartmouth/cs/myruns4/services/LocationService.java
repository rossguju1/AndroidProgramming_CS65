package edu.dartmouth.cs.myruns4.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;

import java.util.List;

import edu.dartmouth.cs.myruns4.models.Constants;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationService extends IntentService {
    protected static final String TAG = LocationService.class.getSimpleName();
    private FusedLocationProviderClient fusedLocationClient;
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FAST_INTERVAL = 1000;
    public static final String BROADCAST_LOCATION = "location update";

    public LocationService() {
        super(TAG);
        Log.d(TAG, TAG + "Location Service Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + "onCreate()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, TAG + "onHandleIntent()");
        Log.d("HERE", "Location Service:" + Thread.currentThread().getId());

        LocationResult res = LocationResult.extractResult(intent);

        if (res != null) {

            List<Location> detectedLocations = res.getLocations();

            for (Location loc : detectedLocations) {
                Log.d(TAG, "Detected location: " + loc.getLongitude() + " " + loc.getLatitude());
                broadcastLocation(loc);
            }
        }
    }


    private void broadcastLocation(Location loc) {
        Log.d(TAG,TAG+ "broadcastLocation()");

        Intent i = new Intent(Constants.BROADCAST_DETECTED_LOCATION);
            i.putExtra("location", loc);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);


    }
}
