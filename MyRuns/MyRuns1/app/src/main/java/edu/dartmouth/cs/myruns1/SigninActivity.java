package edu.dartmouth.cs.myruns1;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.dartmouth.cs.myruns1.models.ProfilePreferences;


public class SigninActivity extends AppCompatActivity {

    public static final String TAG = "tag_log_in";
    private static final String DEBUG_TAG = "debugger";
    private Button mSignInButton;
    private Button mRegisterButton;

    private EditText mEmail;

    private EditText mPassword;

    private ProfilePreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setTitle("Sign In");

        Log.d(DEBUG_TAG, "onCreate()");


        mSignInButton = findViewById(R.id.signinButton);

        mRegisterButton = findViewById(R.id.registerButton);

        mEmail = findViewById(R.id.email1);

        mPassword = findViewById(R.id.password1);


        ProfilePreferences mPreference = new ProfilePreferences(this);

        // check previous user made

        //Fake delay progress bar


        final String auto_email = mPreference.getProfileEmail();

        final String auto_pass = mPreference.getProfilePassword();



        if ((auto_email != "nan") && (auto_pass != "nan")){

            mEmail.setText(auto_email);
            mPassword.setText(auto_pass);


        }


        mSignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){



                if (log_in(auto_email, auto_pass)){
                    Intent intent = new Intent(SigninActivity.this, MainMyRunsActivity.class);
                    startActivity(intent);

                }


            }

        });

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(SigninActivity.this, RegisterProfileActivity.class);
                intent.putExtra(RegisterProfileActivity.INTENT_FROM, TAG);
                startActivity(intent);
            }
        });



    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(DEBUG_TAG,"onRestart()");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
    }

    private boolean log_in(String preference_email, String preference_password){




        mEmail.setError(null);
        mPassword.setError(null);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();


        if ((preference_email == "nan") && (preference_password == "nan")){

            Toast.makeText(getApplicationContext(),
                    "Need to Register a Profile",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (TextUtils.isEmpty(password)){
                mPassword.setError("Password is required");
                mPassword.requestFocus();
                return false;
            } else if (!email.contains("@")){
                mEmail.setError("Email is required");
                mEmail.requestFocus();
                return false;
            }
        }


    return true;

    }



}
