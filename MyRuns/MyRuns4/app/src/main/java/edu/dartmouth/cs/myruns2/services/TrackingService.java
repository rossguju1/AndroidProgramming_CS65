package edu.dartmouth.cs.myruns2.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

import edu.dartmouth.cs.myruns2.MapInputActivity;
import edu.dartmouth.cs.myruns2.models.Constants;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackingService extends Service {
    public static final String TAG = "service";
    public static final String TRACKING_TYPE = "tracking_intent";
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FAST_INTERVAL = 5000;
    private static final int SERVICE_NOTIFICATION_ID =1;
    //public static final String BROADCAST_LOCATION = "location update";
    private NotificationManager notificationManger;
    private PendingIntent mPendingIntent;
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;

    private PendingIntent mPendingIntentAR;
    private ActivityRecognitionClient mActivityRecognitionClient;

    private String mInputType;


    public TrackingService() {
        Log.d(TAG, "TrackingService: Thread ID is:" + Thread.currentThread().getId());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TrackingService: onCreate(): Thread ID is:" + Thread.currentThread().getId());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "TrackingService: onStartCommand(): Thread ID is:" + Thread.currentThread().getId());
        createNotification();

       if (intent.getStringExtra(TRACKING_TYPE).equals("auto")){
           Log.d(TAG, "get intent in sevice: auto");
           mActivityRecognitionClient = new ActivityRecognitionClient(this);
           Intent mIntentServiceAR = new Intent(this, ARService.class);
           mPendingIntentAR = PendingIntent.getService(this,
                   2, mIntentServiceAR, PendingIntent.FLAG_UPDATE_CURRENT);
           requestActivityUpdatesHandler();



       } else if (intent.getStringExtra(TRACKING_TYPE).equals("gps")){

           Log.d(TAG, "get intent in sevice: gps");

       }


        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);


        Log.d(TAG, "onStartCommand()");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent mIntentService = new Intent(this, LocationService.class);
        mPendingIntent = PendingIntent.getService(this,
                1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestLocationUpdatesHandler();






        return START_STICKY;

    }



    private void createNotification() {
        Intent notificationIntent = new Intent(this, MapInputActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Create notification and its channel
        notificationManger = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "tracking";
        String channelName = "MyRuns";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationManger.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("MyRuns")
                .setContentText("Tracking your locations")
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(SERVICE_NOTIFICATION_ID, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TrackingService: onDestroy(): Thread ID is: " + Thread.currentThread().getId());
        removeLocationUpdatesHandler();
        removeActivityUpdatesHandler();
    }

    @Override
    public IBinder onBind(Intent intent) {
       // mInputType = intent.getStringExtra(TRACKING_TYPE);
        Log.d(TAG, "TrackingService: onBind() ");

        return null;
    }

    // request updates and set up callbacks for success or failure
    public void requestLocationUpdatesHandler() {
        Log.d(TAG, "requestActivityUpdatesHandler()");
        if(fusedLocationClient != null){
            Task<Void> task = fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    mPendingIntent);

            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Successfully requested activity updates");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Requesting activity updates failed to start");
                }
            });
        }

    }

    // remove updates and set up callbacks for success or failure
    public void removeLocationUpdatesHandler() {
        if(fusedLocationClient != null){
            Task<Void> task = fusedLocationClient.removeLocationUpdates(mPendingIntent);
            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Removed activity updates successfully!");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to remove activity updates!");
                }
            });
        }
    }

    // request updates and set up callbacks for success or failure
    public void requestActivityUpdatesHandler() {
        Log.d(TAG, "requestActivityUpdatesHandler()");
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                    Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    mPendingIntentAR);

            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Successfully requested activity updates");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Requesting activity updates failed to start");
                }
            });
        }

    }
    public void removeActivityUpdatesHandler() {
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                    mPendingIntentAR);
            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Removed activity updates successfully!");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to remove activity updates!");
                }
            });
        }
    }

}
