package edu.dartmouth.cs.myruns2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.dartmouth.cs.myruns2.fragments.SettingsFragment;

public class MapInputActivity extends AppCompatActivity {
    public static final String MAPINPUT_INTENT_FROM = "mapinputintent_from";
    private GoogleMap mMap;
    private static final String DEBUG_TAG = "MapInputActivity";

    private static final String FROM_MAPINPUT_AUTO = "auto";
    private static final String FROM_MAPINPUT_GPS = "gps";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate() MapInputActivity");
        //Create action bar with back button and name
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Map");

        setContentView(R.layout.activity_map_input);
        if (getIntent().getStringExtra(MAPINPUT_INTENT_FROM).equals("gps")){
            Log.d(DEBUG_TAG, "Mapinput activity from start tab gps");
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra(MapsActivity.MAPS_INTENT_FROM, FROM_MAPINPUT_GPS);

            startActivityForResult(intent, 0);
        }
        if (getIntent().getStringExtra(MAPINPUT_INTENT_FROM).equals("auto")){
            Log.d(DEBUG_TAG, "Mapinput activity from start tab auto");
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra(MapsActivity.MAPS_INTENT_FROM, FROM_MAPINPUT_AUTO);

            startActivityForResult(intent, 0);
        }







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


}
