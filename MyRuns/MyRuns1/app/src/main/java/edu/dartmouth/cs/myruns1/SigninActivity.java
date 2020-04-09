package edu.dartmouth.cs.myruns1;
import edu.dartmouth.cs.myruns1.models.Profiles;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class SigninActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "debugger";
    private Button mSignInButton;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        Log.d(DEBUG_TAG, "onCreate()");


        mSignInButton = findViewById(R.id.signinButton);

        mRegisterButton = findViewById(R.id.registerButton);


        mSignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(SigninActivity.this, MainMyRunsActivity.class);
                startActivity(intent);

            }

        });

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(SigninActivity.this, RegisterProfileActivity.class);
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

    private void saveProfile(){



    }


    private void loadProfile(){



    }




}
