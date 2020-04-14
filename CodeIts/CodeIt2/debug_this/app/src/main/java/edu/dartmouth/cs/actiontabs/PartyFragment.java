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

public class PartyFragment extends Fragment {
	EditText partyTitleView,partyVenueView,partyDateView,partyTimeView;
	DatePickerDialog.OnDateSetListener partyDatePicke;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.chatfragment, container, false);
         partyTitleView=v.findViewById(R.id.party_title);
         partyDateView=v.findViewById(R.id.party_date);
         partyTimeView =v.findViewById(R.id.party_time);
         partyDateView.setKeyListener(null);
         partyTimeView.setKeyListener(null);

        final Button partySaveBtn=v.findViewById(R.id.checkBox1);
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
        partyDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                new DatePickerDialog(getActivity(), partyDatePicke, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
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



    DatePickerDialog.OnDateSetListener partyDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateView();

        }

    };

    public Calendar cal;
    private void updateDateView(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        partyDateView.setText(sdf.format(cal.getTime()));
    }

}

