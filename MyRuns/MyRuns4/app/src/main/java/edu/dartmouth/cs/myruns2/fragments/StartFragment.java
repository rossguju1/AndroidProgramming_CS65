package edu.dartmouth.cs.myruns2.fragments;


import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.dartmouth.cs.myruns2.ManualInputActivity;
import edu.dartmouth.cs.myruns2.MapInputActivity;
import edu.dartmouth.cs.myruns2.R;
import edu.dartmouth.cs.myruns2.fragments.HistoryFragment;

public class StartFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String DEBUG_TAG = "StartFragment";
    private FloatingActionButton mStartButton;
    private Spinner mInput;
    private Spinner mActivity;
    private String input = "";
    private String activity = "";
    private static final String FROM_START_TAB = "start_tab";
    private static final String FROM_START_TAB_GPS = "gps";
    private static final String FROM_START_TAB_AUTO = "auto";


    public static final String START_INSERT_ITEM = "start_insert_item";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_fragment, container, false);
        //Get our page buttons/spinners
        mStartButton = (FloatingActionButton) v.findViewById(R.id.startButton);
        mInput = (Spinner) v.findViewById(R.id.input_spinner);
        mActivity = (Spinner) v.findViewById(R.id.activity_spinner);

        //Set our selection listeners
        mInput.setOnItemSelectedListener(this);
        mActivity.setOnItemSelectedListener(this);

        // Create a listener to launch the appropriate action based on current spinner option
        mStartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Get the text from our spinners
                if(mInput != null && mActivity != null) {
                    input = mInput.getSelectedItem().toString();
                    activity = mActivity.getSelectedItem().toString();
                    Intent intent = null;
                    if(input.equals("Manual")){
                        intent = new Intent(getActivity(), ManualInputActivity.class);
                        intent.putExtra(ManualInputActivity.MANUAL_INTENT_FROM, FROM_START_TAB);
                    } else if(input.equals("Automatic")){
                        intent = new Intent(getActivity(), MapInputActivity.class);
                        intent.putExtra(MapInputActivity.FROM_MAPINPUT, FROM_START_TAB_AUTO);

                    } else if(input.equals("GPS")){
                        intent = new Intent(getActivity(), MapInputActivity.class);
                        intent.putExtra(MapInputActivity.FROM_MAPINPUT, FROM_START_TAB_GPS);

                    }

                    //Here we make sure our intent has be initialized and then pass in activity as extra string
                    if(intent != null){
                        //Here we pass in the name of the currently selected activity
                        intent.putExtra("activity_name", activity);
                        //startActivity(intent);
                        startActivityForResult(intent, 2);
                    }
                }
            }
        });
        return v;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(mInput.getSelectedItem().toString().equals("Automatic")){
            mActivity.setEnabled(false);
        }else {
            mActivity.setEnabled(true);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // If nothing is selected we don't care but this method is required for the interface
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(DEBUG_TAG, "IN START FRAGMENT  " + requestCode);

        if (requestCode == 2){
            if(data != null){
                String _id = data.getStringExtra(START_INSERT_ITEM);
                long id = Long.parseLong(_id);
                Log.d(DEBUG_TAG, "onACTIVITYResult in MainMyRuns:  ID= " + Long.parseLong(_id));
                HistoryFragment.mAdapter.onActivityResult(id);
            }else {
                Log.d(DEBUG_TAG, "Error onACTIVITYResult in MainMyRuns retrieving ID");
            }

        }
    }


}

