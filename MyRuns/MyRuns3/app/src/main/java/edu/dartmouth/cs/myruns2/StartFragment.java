package edu.dartmouth.cs.myruns2;


import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StartFragment extends Fragment {

    private FloatingActionButton mStartButton;
    private Spinner mInput;
    private Spinner mActivity;
    private String input = "";
    private String activity = "";
    private static final String FROM_START_TAB = "start_tab";

//    EditText partyTitleView,partyVenueView,partyDateView,partyTimeView;
    //DatePickerDialog.OnDateSetListener partyDatePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_fragment, container, false);
        //Get our page buttons/spinners
        mStartButton = (FloatingActionButton) v.findViewById(R.id.startButton);
        mInput = (Spinner) v.findViewById(R.id.input_spinner);
        mActivity = (Spinner) v.findViewById(R.id.activity_spinner);

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
                    } else if(input.equals("GPS")){
                        intent = new Intent(getActivity(), MapInputActivity.class);
                    }

                    //Here we make sure our intent has be initialized and then pass in activity as extra string
                    if(intent != null){
                        //Here we pass in the name of the currently selected activity
                        intent.putExtra("activity_name", activity);
                        startActivity(intent);
                    }
                }
            }
        });
        return v;
    }
}

