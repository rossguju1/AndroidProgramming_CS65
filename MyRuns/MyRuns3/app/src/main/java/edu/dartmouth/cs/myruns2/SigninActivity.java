package edu.dartmouth.cs.myruns2;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.dartmouth.cs.myruns2.models.ProfilePreferences;


public class SigninActivity extends AppCompatActivity {

    public static final String TAG = "tag_log_in";
    private static final String DEBUG_TAG = "debugger";
    private Button mSignInButton;
    private Button mRegisterButton;

    private EditText mEmail;

    private EditText mPassword;

    private ProfilePreferences mPreference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate()");

        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setTitle("Sign In");

        mSignInButton = findViewById(R.id.signinButton);

        mRegisterButton = findViewById(R.id.registerButton);
        mEmail = findViewById(R.id.email1);
        mPassword = findViewById(R.id.password1);
        mPreference = new ProfilePreferences(this);

        // fill in the email and password if the user previously registered
        if ((mPreference.getProfileEmail() != "nan") && (mPreference.getProfilePassword() != "nan")){
            mEmail.setText(mPreference.getProfileEmail());
            mPassword.setText(mPreference.getProfilePassword());
        }

        // if the user clicks on the sign in button we check login requirements and launch the
        // main activity if successful sign in
        mSignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (log_in(mPreference.getProfileEmail(), mPreference.getProfilePassword())){
                    Intent intent = new Intent(SigninActivity.this, MainMyRunsActivity.class);
                    startActivity(intent);
                }
            }
        });

        // if the user clicks register, we create and launch the Register profile Activity
        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(SigninActivity.this, RegisterProfileActivity.class);
                intent.putExtra(RegisterProfileActivity.INTENT_FROM, TAG);
                //startActivityForResult(i, PICK_IMAGE);
                startActivity(intent);
            }
        });
    }


    //checks login requirements
    // returns true if the log in requirements are valid
    // otherwise false
    private boolean log_in(String preference_email, String preference_password){
        // set error messages for the views to nothing
        mEmail.setError(null);
        mPassword.setError(null);
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        //check if the edit text entries are empty
        if ((preference_email == "nan") && (preference_password == "nan")){
            Toast.makeText(getApplicationContext(),
                    "Need to Register a Profile",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)){
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return false;
        } else if (!email.contains("@")){
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return false;
        } else if(!email.equals(preference_email)){
            mEmail.setError("Wrong Email");
            mEmail.requestFocus();
            return false;
        } else if(!password.equals(preference_password)){
            mPassword.setError("Wrong Password");
            mPassword.requestFocus();
            return false;
        }
        return true;
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
        mPreference = new ProfilePreferences(this);
        if (mPreference != null) {
            if ((mPreference.getProfileEmail() != "nan") && (mPreference.getProfilePassword() != "nan")) {

                mEmail = findViewById(R.id.email1);

                mPassword = findViewById(R.id.password1);

                mEmail.setText(mPreference.getProfileEmail());
                mPassword.setText(mPreference.getProfilePassword());

            }
        }
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
}