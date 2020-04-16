package edu.dartmouth.cs.actiontabs;

import android.app.DatePickerDialog;
import androidx.fragment.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.SharedPreferences;

public class PartyFragment extends Fragment {
	EditText partyTitleView,partyVenueView,partyDateView,partyTimeView;
    public Calendar cal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.partyfragment, container, false);
         partyTitleView = (EditText) v.findViewById(R.id.party_title);
        partyVenueView = (EditText) v.findViewById(R.id.venue);
         partyDateView= (EditText) v.findViewById(R.id.party_date);
         partyTimeView = (EditText) v.findViewById(R.id.party_time);
         partyDateView.setKeyListener(null);
         partyTimeView.setKeyListener(null);

        cal = Calendar.getInstance();

        final Button partySaveBtn=v.findViewById(R.id.save_party_btn);
        partySaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String partyTitle= partyTitleView.getText().toString();
               String partyVenue=partyVenueView.getText().toString();
               String partyDate=partyDateView.getText().toString();
               String partyTime=partyTimeView.getText().toString();

               if(!partyTitle.isEmpty() && !partyVenue.isEmpty() && !partyDate.isEmpty() && !partyTime.isEmpty()){
                   partyTitleView.getText().clear();
                   partyVenueView.getText().clear();
                   partyDateView.getText().clear();
                   partyTimeView.getText().clear();
                   Toast.makeText(getActivity(),"Party Schedule Added!",Toast.LENGTH_LONG).show();


               }else{

                   Toast.makeText(getActivity(),null,Toast.LENGTH_LONG).show();
               }
            }
        });
        // This is my own implementation for the party Date
        partyDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog  StartTime = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        partyDateView.setText( monthOfYear + "/" + dayOfMonth + "/" + year);

                    }

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                StartTime.setTitle("Select Party Date");
                StartTime.show();
            }
        });


        partyTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog  partyTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int partyHour, int partyMinute) {
                        partyTimeView.setText( partyHour + ":" + partyMinute);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                partyTimePicker.setTitle("Select Party Time");
                partyTimePicker.show();
            }
        });


        return v;
    }


}

