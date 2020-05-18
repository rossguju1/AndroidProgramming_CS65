package edu.dartmouth.cs.myruns4.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.dartmouth.cs.myruns4.MapInputActivity;
import edu.dartmouth.cs.myruns4.models.Constants;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackingService extends Service {
    public static boolean isRunning = false;
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
    private int from_type;

    private String mInputType;

    private int input_type = -1;

    private int mMessage = -1;

    private boolean Was_Paused = false;



    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps
    // track of
    // all
    // current
    // registered
    // clients.


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
        isRunning = true;

//       if (intent.getStringExtra(TRACKING_TYPE).equals("auto")){
//        createARService();
//
//       } else if (intent.getStringExtra(TRACKING_TYPE).equals("gps")){
//
//           Log.d(TAG, "get intent in sevice: gps");
//
//
//       }
//
//        createLocationService();
//
//


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
                    Log.d(TAG, "Successfully requested Location updates");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TrackingService: onDestroy(): Thread ID is: " + Thread.currentThread().getId());
        removeLocationUpdatesHandler();
        removeActivityUpdatesHandler();
        notificationManger.cancelAll(); // Cancel the persistent notification.
        isRunning = false;
    }

    private class IncomingMessageHandler extends Handler {
        // must implement this to receive messages.
        // here you get the Message from the Messenger recovered by msg.replyTo (or Messenger used by the client
        // or MainActivity.java). The Messenger from a certain client is stored in a list when the client binds to
        // the server, and is removed from the list when the bind is terminated.
        // *** the message receives from this Handler is also carried by the server's mMessenger defined in
        // line 49 in this file. See line 127 in MainActivity.java

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "S:handleMessage()");
            switch (msg.what) {
                case Constants.MSG_REGISTER_CLIENT:
                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.add(msg.replyTo) ");
                    mClients.add(msg.replyTo);//replyTo is the Messanger, that carrys the Message over.
                    input_type = msg.arg1;
                    from_type = msg.arg2;

                    Log.d(TAG, "MSG REGISTER_CLIENT input type: " + input_type);

                    if (input_type == Constants.MSG_AUTO){
                        Log.d(TAG, "Creating AR and Location Service");

                        createARService();
                        createLocationService();
                        sendMessageToUI(input_type);
                    } else if(input_type == Constants.MSG_GPS){
                        Log.d(TAG, "Creating ONLY Location Service");

                        createLocationService();
                        sendMessageToUI(input_type);
                    }
                    break;
                case Constants.MSG_UNREGISTER_CLIENT:
                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.remove(msg.replyTo) ");
                    mClients.remove(msg.replyTo);// each client has a dedicated Messanger to communicae with ther server.
                    input_type = msg.arg1;
                    break;
                case Constants.MSG_SET_INT_VALUE:
                    mMessage = msg.arg1;
                    sendMessageToUI(mMessage);

//                    if(mMessage == Constants.MSG_PAUSE){
//                        Was_Paused = true;
//                    }else if(mMessage == Constants.MSG_RESUME && Was_Paused){
//
//
//                        sendMessageToUI(from_type);
//                        sendMessageToUI(input_type);
//                        Was_Paused = false;
//                    } else{
//                        sendMessageToUI(mMessage);
//                    }
                    Log.d(TAG, "S:handleMessage: " + msg.what + msg.replyTo +" message todo received:  " +  mMessage);

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "S:onBind() - return mMessenger.getBinder()");
        // getBinder()
        // Return the IBinder that this Messenger is using to communicate with
        // its associated Handler; that is, IncomingMessageHandler().
        // *** onBind() is only called once when a bind is established (e.g. bindService()
        // is called by the client. onBind() wont be called again no matter how many times
        // bindService() is called unless the service is stopped (e.g. stopService()) and started again.
        return mMessenger.getBinder();// Retrieve the IBinder that this Messenger is using to communicate with its associated Handler.

    }


    private void sendMessageToUI(int intvaluetosend) {
        Log.d(TAG, "S:sendMessageToUI" + + mClients.size());
        Iterator<Messenger> messengerIterator = mClients.iterator();
        // after BIND TO SERVICE is clicked mClients.size() is 1; after UNBIND FROM SERVICE is
        // clicked, mClients.size() is 0. Messenger is used to send(Message)
        while (messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {
                // Send data as an Integer
                Log.d(TAG, "S:TX MSG_SET_INT_VALUE");

                // arg1 and arg2 are lower-cost alternatives to using setData() if you only need to store a few integer values.
                // public static Message obtain(Handler h, int what, int arg1, int arg2) what - User-defined message code so that the recipient can identify what this message is about.
                Message msg_int = Message.obtain(null, Constants.MSG_SET_INT_VALUE, intvaluetosend, 0);
                messenger.send(msg_int);

//                Bundle bundle = new Bundle();//Bundle is generally used for passing data between various activities of android. It depends on you what type of values you want to pass, but bundle can hold all types of values, and pass to the new activity.
//                bundle.putString("str1", "ab" + intvaluetosend + "cd");
//                // you need to tell the client what type of data it receives.Here it is MSG_SET_STRING_VALUE type.
//                Message msg_str = Message.obtain(null, MSG_SET_STRING_VALUE);
//                msg_str.setData(bundle);
//                Log.d(TAG, "S:TX MSG_SET_STRING_VALUE");
//                messenger.send(msg_str);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private void createLocationService(){

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
    }

    private void createARService(){

        Log.d(TAG, "get intent in sevice: auto");
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Intent mIntentServiceAR = new Intent(this, ARService.class);
        mPendingIntentAR = PendingIntent.getService(this,
                1, mIntentServiceAR, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesHandler();
    }

}
