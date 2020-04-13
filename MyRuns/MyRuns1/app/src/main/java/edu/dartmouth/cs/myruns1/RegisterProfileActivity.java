package edu.dartmouth.cs.myruns1;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;


import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.soundcloud.android.crop.Crop;

import edu.dartmouth.cs.myruns1.models.ProfilePreferences;


public class RegisterProfileActivity extends AppCompatActivity {

    private static final int ERROR_CAMERA_KEY = 225;
    private static final int PICK_IMAGE = 77;
    public static final String INTENT_FROM = "from";
    private Button mChangeButton;

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private boolean isTakenFromCamera;


    private EditText mEditName;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mMale;
    private RadioButton mFemale;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditPhoneNumber;
    private EditText mMajor;
    private EditText mClassYear;

    String PicturePath;


    private Bitmap rotatedBitmap;
    File photoFile = null;

    public ProfilePreferences mPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setUpActionBar();

        //Display back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //setUpActionBar();

        setContentView(R.layout.activity_profile);

        mChangeButton = findViewById(R.id.btnChangePhoto);

        mImageView = (ImageView) findViewById(R.id.imageProfile);

        mEditName = (EditText) findViewById(R.id.editName);

        mRadioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);

        mMale = (RadioButton) findViewById(R.id.radioGenderM);

        mFemale = (RadioButton) findViewById(R.id.radioGenderF);

        mEditEmail = (EditText) findViewById(R.id.editEmail);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditPhoneNumber = (EditText) findViewById(R.id.editPhone);

        mMajor  = (EditText) findViewById(R.id.editMajor);

        mClassYear = (EditText) findViewById(R.id.editClassYear);

        mPreference = new ProfilePreferences(this);



        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                mImageView.setImageBitmap(bitmap);
            }  catch (IOException e) {
                e.printStackTrace();
            }
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
        return super.onCreateOptionsMenu(menu);
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
            saveProfile();
            //finish();


            return true;
        } else if (id == android.R.id.home){
            Toast.makeText(getApplicationContext(),"Hit BACK!!!!",
                    Toast.LENGTH_SHORT).show();
            finish();
            return true;

        }else{
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
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;

        }

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
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA}, 0);

                        }
                    });
                    builder.show();
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

                Bitmap rotatedBitmap = imageOrientationValidator(photoFile);
                try {
                    FileOutputStream fOut = new FileOutputStream(photoFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Send image taken from camera for cropping
                mImageCaptureUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID,
                        photoFile);


                beginCrop(mImageCaptureUri);
                break;

            case Crop.REQUEST_CROP: //We changed the RequestCode to the one being used by the library.
                // Update image view after image crop
                handleCrop(resultCode, data);


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
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID,
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
                //intent.putExtra("return-data", true);
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
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri destination = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID,
                    photoFile);
            Log.d("URI: ", destination.toString());
            Crop.of(source, destination).asSquare().start(this);
        }

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.d("Error", "error");
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void saveProfile(){


        mEditName.setError(null);
        mEditEmail.setError(null);
        mEditPassword.setError(null);



        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();

        int gender_selected = -1;
        boolean malechecked =  mMale.isChecked();
        boolean femalechecked =  mFemale.isChecked();

        if (malechecked){
            gender_selected = 1;
            
        } else if (femalechecked){
            gender_selected = 0;
        }

        String phone = mEditPhoneNumber.getText().toString();
        String major = mMajor.getText().toString();
        String class_year = mClassYear.getText().toString();

        boolean canceled = false;

        View focusView = null;

       // if (m)

        if (TextUtils.isEmpty(name)){
            mEditName.setError("Need to add name");
            focusView =  mEditName;
            canceled = true;
        }

        if (TextUtils.isEmpty(password)){
            mEditPassword.setError("Need a password for email");
            focusView = mEditPassword;
            canceled = true;

        } else if(isPasswordValid(password) == false){
            mEditPassword.setError("This email password is wrong");
            focusView = mEditPassword;
            canceled = true;

        }

        if(TextUtils.isEmpty(email)){
            mEditEmail.setError("This field is required");
            focusView = mEditEmail;
            canceled = true;
        } else if (isEmailValid(email) == false){
            mEditEmail.setError("Email is not valid");
            focusView = mEditEmail;
            canceled = true;

        }

        if(gender_selected == -1){

            focusView = mRadioGenderGroup;
            canceled = true;
        }

        if (TextUtils.isEmpty(phone)){
            mEditPhoneNumber.setError("This field is required");
            focusView = mEditPhoneNumber;
            canceled = true;
        }
        if (TextUtils.isEmpty(class_year)){
        mClassYear.setError("This field is required");
        focusView = mClassYear;
        canceled = true;
    }

        if(TextUtils.isEmpty(major)){

            mMajor.setError("This field is required");
            focusView = mMajor;
            canceled = true;
        }

        if (canceled){

            if (focusView instanceof EditText) {
                focusView.requestFocus();
            } else if(focusView instanceof RadioGroup){

                Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show();


            }
        }else{
            // save profile in preferences
            mPreference.clearProfilePreferences();
            mPreference.setProfileName(name);
            mPreference.setProfileEmail(email);
            mPreference.setProfilePassword(password);
            mPreference.setProfileGender(gender_selected);
            mPreference.setProfilePhone(phone);
            mPreference.setProfileMajor(major);
            mPreference.setProfileClassYear(class_year);
            mPreference.setProfilePicture(mImageCaptureUri.toString());

            /*
            if (!mPicPath.equalsIgnoreCase("nan")){
                mPreference.setProfilePicture(PicPath);
            }

             */


            finish();

        }

    }

private boolean isPasswordValid(String password){

        if (password.length() < 6){
            return false;
        }

        return true;
}

private boolean isEmailValid(String email){

        return email.contains("@");

}

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private Bitmap imageOrientationValidator(File photoFile) {
        ExifInterface ei;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID,
                    photoFile));
            ei = new ExifInterface(photoFile.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;

                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


}
