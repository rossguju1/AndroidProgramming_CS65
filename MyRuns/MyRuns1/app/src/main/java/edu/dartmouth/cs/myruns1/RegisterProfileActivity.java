package edu.dartmouth.cs.myruns1;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;


public class RegisterProfileActivity extends AppCompatActivity {

    private static final int ERROR_CAMERA_KEY = 225;
    private static final int PICK_IMAGE = 77;
    private Button mChangeButton;

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private boolean isTakenFromCamera;


    private EditText mEditName;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mRadioGender;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditPhoneNumber;
    private EditText mMajor;
    private EditText mClassYear;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mChangeButton = findViewById(R.id.btnChangePhoto);

        mImageView = (ImageView) findViewById(R.id.imageProfile);

        mEditName = (EditText) findViewById(R.id.editName);

        mRadioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);

        mEditEmail = (EditText) findViewById(R.id.editEmail);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditPhoneNumber = (EditText) findViewById(R.id.editPhone);

        mMajor  = (EditText) findViewById(R.id.editMajor);

        mClassYear = (EditText) findViewById(R.id.editClassYear);




        //String value = editText.getText().toString();

        //int selectedId = radioSexGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        //radioSexButton = (RadioButton) findViewById(selectedId);


        //mRadioFemale
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        }

        loadSnap();

        mChangeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkPermissions();
                displayDialog(MyRunsDialogFragment.DIALOG_ID_PHOTO_PICKER);

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
        if (id == R.id.btnSave) {

            // Save picture
            saveSnap();
            // Making a "toast" informing the user the picture is saved.
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ui_profile_toast_save_text),
                    Toast.LENGTH_SHORT).show();
            // Close the activity
            finish();


            return true;
        } else{

            Toast.makeText(getApplicationContext(),
                    getString(R.string.ui_profile_registration_incomplete),
                    Toast.LENGTH_SHORT).show();


        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Code to check for runtime permissions.
     */
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    // ****************** button click callbacks ***************************//




    // Handle data after activity returns.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        mImageView.setImageDrawable(null);

        switch (requestCode) {
            case PICK_IMAGE:
                Toast.makeText(this, "here2", Toast.LENGTH_SHORT).show();
                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
                break;


            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                break;

            case Crop.REQUEST_CROP: //We changed the RequestCode to the one being used by the library.
                // Update image view after image crop
                handleCrop(resultCode, data);

                // Delete temporary image taken by camera after crop.
                if (isTakenFromCamera) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                }

                break;
        }
    }

    // ******* Photo picker dialog related functions ************//

    public void displayDialog(int id) {
        DialogFragment fragment = MyRunsDialogFragment.newInstance(id);
        //fragment.show(getSupportFragmentManager(), getString(R.string.dialog_fragment_tag_photo_picker));
        // Alternatively I can create a transaction and drive the lifecycle -- same as show()
        getSupportFragmentManager().beginTransaction()
                .add(fragment, getString(R.string.dialog_fragment_tag_photo_picker))
                .commit();
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case MyRunsDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
                // Take photo from camera，
                // Construct an intent with action
                // MediaStore.ACTION_IMAGE_CAPTURE
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken
                // photo
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                /**
                 This was the previous code to generate a URI. This was throwing an exception -
                 "android.os.StrictMode.onFileUriExposed" in Android N.
                 This was because StrictMode prevents passing URIs with a file:// scheme. Once you
                 set the target SDK to 24, then the file:// URI scheme is no longer supported because the
                 security is exposed. You can change the  targetSDK version to be <24, to use the following code.
                 The new code as written above works nevertheless.


                 mImageCaptureUri = Uri.fromFile(new File(Environment
                 .getExternalStorageDirectory(), "tmp_"
                 + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                 **/

                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                isTakenFromCamera = true;
                break;
            case MyRunsDialogFragment.DIALOG_ID_PHOTO_PICKER:

                Toast.makeText(this, "here1", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);


            default:
                return;
        }

    }

    // ****************** private helper functions ***************************//

    private void loadSnap() {


        // Load profile photo from internal storage
        try {
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mImageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    private void saveSnap() {

        // Commit all the changes into preference file
        // Save profile image into internal storage.

        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /** Method to start Crop activity using the library
     *	Earlier the code used to start a new intent to crop the image,
     *	but here the library is handling the creation of an Intent, so you don't
     * have to.
     *  **/
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
