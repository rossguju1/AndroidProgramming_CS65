package edu.dartmouth.cs.myruns1;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterProfileActivity extends AppCompatActivity {

    private static final int ERROR_CAMERA_KEY = 225;
    private Button mChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mChangeButton = findViewById(R.id.change_button);

        mChangeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(RegisterProfileActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(RegisterProfileActivity.this, "Cant get your Camera", Toast.LENGTH_SHORT).show();


                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterProfileActivity.this,
                            Manifest.permission.CAMERA)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Toast.makeText(RegisterProfileActivity.this, "Camera is needed for this app", Toast.LENGTH_SHORT).show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(RegisterProfileActivity.this,
                                new String[]{Manifest.permission.CAMERA}, ERROR_CAMERA_KEY);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    Toast.makeText(RegisterProfileActivity.this, "Accessing Camera", Toast.LENGTH_SHORT).show();

                }

            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.register_button) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPhotoPickerItemSelected(int item) {


    }
}
