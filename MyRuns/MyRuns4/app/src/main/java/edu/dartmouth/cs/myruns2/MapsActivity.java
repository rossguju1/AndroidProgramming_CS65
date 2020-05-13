package edu.dartmouth.cs.myruns2;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.dartmouth.cs.myruns2.services.TrackingService;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String MAPS_INTENT_FROM = "mapintent_from";
    private static final String DEBUG_TAG = "MapActivity";
    private GoogleMap mMap;
    private int PERMISSION_REQUEST_CODE = 1;
    private Marker mMaker;
    private Intent serviceIntent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(DEBUG_TAG, "onCreate() Map Activity");

       setContentView(R.layout.activity_map_input);

        if (getIntent().getStringExtra(MAPS_INTENT_FROM).equals("gps")){
            Log.d(DEBUG_TAG, "onCreate Map activity gps");
        }
        if (getIntent().getStringExtra(MAPS_INTENT_FROM).equals("auto")){
            Log.d(DEBUG_TAG, "onCreate Map activity auto");
        }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);
        if (!checkPermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        else
            startTrackingService();

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcastReceiver,
                new IntentFilter(TrackingService.BROADCAST_LOCATION));
    }

    BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackingService.BROADCAST_LOCATION)) {
                Log.d(TrackingService.TAG, "MapsActivity: onReceive(): Thread ID is:" + Thread.currentThread().getId());
                Location location = intent.getParcelableExtra("location");
                LatLng iAmHere = new LatLng(location.getLatitude(), location.getLongitude());
                mMaker.remove();
                mMaker = mMap.addMarker(new MarkerOptions().position(iAmHere).title("I am home"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(iAmHere, 17));
            }

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in the Dartmouth Green and move the camera
        LatLng dartmouth = new LatLng(43.703354, -72.288566);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMaker = mMap.addMarker(new MarkerOptions().position(dartmouth).title("The Green sez hiya Androidistas!"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dartmouth, 17));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackingService();
        } else {
            finish();
        }
    }

    private void startTrackingService() {
        serviceIntent = new Intent(this, TrackingService.class);
        startForegroundService(serviceIntent);
    }

    //******** Check run time permission for locationManager. This is for v23+  ********
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }
    //****** Check run time permission ************
}
