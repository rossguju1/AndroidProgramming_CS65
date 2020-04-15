package edu.dartmouth.cs.actiontabs;

import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FindFragment extends Fragment {

    SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.findfragment, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final EditText emailView= v.findViewById(R.id.editEmail);
        final EditText phoneView = (EditText) v.findViewById(R.id.editPhone);
        final EditText birthDateView= v.findViewById(R.id.editText1);
        final RadioGroup genderView=v.findViewById(R.id.radioGender);
        final RatingBar ratingBar=(RatingBar) v.findViewById(R.id.ratingBar1);
        final ToggleButton cellPhoneButton=v.findViewById(R.id.toggleButton1);
        final CheckBox checkBox=v.findViewById(R.id.checkBox1);
        Button saveButton =v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= emailView.getText().toString();
                String phone=phoneView.getText().toString();
                String birthDate=birthDateView.getText().toString();
                int genderId= genderView.getCheckedRadioButtonId();
                String gender;
                if (genderId < 0){
                    gender="";
                } else{
                    RadioButton radioButton = (RadioButton)genderView.findViewById(genderId);
                    gender=radioButton.getText().toString();

                }

                float rating= ratingBar.getRating();
                boolean phoneOff= cellPhoneButton.isChecked();
                boolean aForElse= checkBox.isChecked();
                saveRecords(email,phone,gender,birthDate,rating,phoneOff, aForElse);
                emailView.getText().clear();
                phoneView.getText().clear();
                genderView.clearCheck();
                birthDateView.getText().clear();
                checkBox.setChecked(false);
                cellPhoneButton.setChecked(false);
                ratingBar.setRating(0F);
                Toast.makeText(getActivity(),getString(R.string.updated_profile),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void saveRecords(String email,String phone,String gender,String birthDate,float rating,boolean phoneOff, boolean aForElse){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("gender", gender);
        editor.putString("birthDate",birthDate);
        editor.putFloat("rating", rating);
        editor.putBoolean("phoneOff", phoneOff);
        editor.putBoolean("aForElse", aForElse);
        editor.apply();


    }

    
}

