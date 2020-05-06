package edu.dartmouth.cs.myruns2;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.dartmouth.cs.myruns2.models.ProfilePreferences;


public class RegisterProfileActivity extends AppCompatActivity {

    private static final int ERROR_CAMERA_KEY = 225;
    private static final int PICK_IMAGE = 77;
    private static final String URI_STATE_KEY = "saved_uri";
    private static final String BITMAP_STATE_KEY = "saved_bitmap";
    private static final String DEBUG_TAG = "RegisterProfileActivity";
    private static final String FROM_SIGNIN = "sign_in_parent";
    private Button mChangeButton;
    private Uri mImageUri = null;

    private ImageView mImageV;
    private EditText mEditName;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mMale;
    private RadioButton mFemale;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditPhoneNumber;
    private EditText mMajor;
    private EditText mClassYear;
    private Bitmap rotatedBitmap;
    private boolean passwordChanged = false;
    private String initialPassword;

    public static final String INTENT_FROM = "from";
    public static final int REQUEST_TAKE_PICTURE_FROM_CAMERA = 0;
    public ProfilePreferences mProfilePreference;

    String PicturePath;
    //Used for storage & compression from camera
    File mPhotoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate()","*** on create fired");
        super.onCreate(savedInstanceState);

        //Display back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile);

        mProfilePreference = new ProfilePreferences(getApplicationContext());

        mImageV = (ImageView) findViewById(R.id.imageProfile);

        loadSnap();

        if (savedInstanceState != null) {
            mImageUri = savedInstanceState.getParcelable(URI_STATE_KEY);
            loadSnapTemp();
        }

        //Get Views by IDs
        mChangeButton = findViewById(R.id.btnChangePhoto);
        mEditName = (EditText) findViewById(R.id.editName);
        mRadioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
        mMale = (RadioButton) findViewById(R.id.radioGenderM);
        mFemale = (RadioButton) findViewById(R.id.radioGenderF);
        mEditEmail = (EditText) findViewById(R.id.editEmail);
        mEditPassword = (EditText) findViewById(R.id.editPassword);
        mEditPhoneNumber = (EditText) findViewById(R.id.editPhone);
        mMajor  = (EditText) findViewById(R.id.editMajor);
        mClassYear = (EditText) findViewById(R.id.editClassYear);

        if (getIntent().getStringExtra(INTENT_FROM).equals("from_main_activity")) {
            loadProfile();
            //If we are coming from the main activity, they shouldn't be able to edit email
            mEditEmail.setKeyListener(null);
            //Need to check if password changes after we have loaded the profile
            initialPassword = mEditPassword.getText().toString();
        }

        mChangeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkPermissions();
                displayDialog(MyRunsDialogFragment.DIALOG_ID_PHOTO_ITEM);
            }
        });
        Log.d("onCreate()","*** end of onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        //Set the appropriate button title depending on navigation context
        if(getIntent().getStringExtra(INTENT_FROM).equals("from_main_activity")){
            menu.getItem(0).setTitle("Save");
        }else {
            menu.getItem(0).setTitle("Register");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu bar clicks
        int id = item.getItemId();

        if (id == R.id.buttonSave) {
            //Save the pic we just took
            //"Toast": tell user pic is saved
            Toast.makeText(getApplicationContext(), getString(R.string.profile_save_text), Toast.LENGTH_SHORT).show();
            // Exit/Close & save active only if fields have been filled appropriately
            if (saveProfile() == false){
                //If we are coming from the main activity, i.e editing our profile && password was changed
                if (getIntent().getStringExtra(INTENT_FROM).equals("from_main_activity") && passwordChanged) {
                    //return to sign in screen
                    Intent intent = new Intent(this, SigninActivity.class);
                    // Add flags to clear stack preventing 'back' action to main activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
            return true;
        } else if (id == android.R.id.home){
            //On home button click
            finish();
            return true;
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.profile_registration_incomplete), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Here we check our runtime permissions for the camera/photos etc.
    private void checkPermissions() {
        //Check for appropriate version
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        //Check if permission has been granted
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                PackageManager.PERMISSION_GRANTED && grantResults[2] ==
                PackageManager.PERMISSION_GRANTED) {
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] ==
                PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        }
                    });

                    builder.show();

                }else {
                    //Permission is denied... sad :(
                    //App must continue on without camera/photo access
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("onSaveInstanceState()","*** attempting to save");
        super.onSaveInstanceState(outState);
        saveSnapTemp();
        //Stoppage in lifecycle, must save image uri
        if(mImageUri != null) {
            outState.putParcelable(URI_STATE_KEY, mImageUri);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mImageUri = savedInstanceState.getParcelable(URI_STATE_KEY);
        }
        //We want to attempt to reload our temp profile pic if a pause occurred in the lifecycle
        loadSnapTemp();
    }

    // ****************** button click callbacks ***************************//
    // Handle data after activity returns.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()","attempt to launch on activity result");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case Crop.REQUEST_CROP:
                //We changed the RequestCode to the one being used by the library.
                handleCrop(resultCode, data);
                break;
            case PICK_IMAGE:
                mImageUri = data.getData();
                beginCrop(mImageUri);
                break;
            case REQUEST_TAKE_PICTURE_FROM_CAMERA:
                // Crop image taken by camera
                Bitmap rotatedBitmap = imageOrientationValidator(mPhotoFile);
                try {
                    mPhotoFile = createImageFile();
                    FileOutputStream fOut = new FileOutputStream(mPhotoFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Set mImageCaptureUri so we can save the state if stop in lifecycle
                mImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mPhotoFile);
                beginCrop(mImageUri);
                break;
        }
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;
        switch (item) {
            case MyRunsDialogFragment.ID_PHOTO_ITEM_FROM_CAMERA:
                // Explicit intent used to take photo
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    // File to store our image
                    mPhotoFile = createImageFile();
                } catch (IOException ex) {
                    // Failed to create file, error occured
                    ex.printStackTrace();
                }

                //Prevent error if failed to create file
                if (mPhotoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID,
                            mPhotoFile);
                    //Handle extra output case (see Android documentation)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }

                try {
                    // Take photo, use tag to ID action in onActivityResult() on return
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case MyRunsDialogFragment.DIALOG_ID_PHOTO_ITEM:
                try {
                    //Create file to save photo
                    mPhotoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                //Intent used when selecting from image gallery i.e picking image
                Intent i = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE);
            default:
                return;
        }

    }


    public void displayDialog(int id) {
        //Dialogue fragment for photo gallery photo selection
        DialogFragment fragment = MyRunsDialogFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .add(fragment, getString(R.string.dialog_fragment_tag_photo_picker))
                .commit();
    }


    private void loadSnap() {
        if (getIntent().getStringExtra(INTENT_FROM).equals("tag_log_in")){
            mImageV.setImageDrawable(null);
            mImageV.setImageResource(R.drawable.ic_launcher_background);
        } else {
            try {
                //Access internal storage fileInput
                FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
                Bitmap bmap = BitmapFactory.decodeStream(fis);
                bmap = bitmapToCircle(bmap);
                mImageV.setImageBitmap(bmap);
                fis.close();
            } catch (IOException e) {
                mImageV.setImageDrawable(null);
                mImageV.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    //Here we are loading an unsaved bitmap for when the activity is paused/stopped but the user
    //hasn't clicked save yet
    private void loadSnapTemp() {
        try {
            //Access internal storage fileInput
            FileInputStream fis = openFileInput(getString(R.string.temp_profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            bmap = bitmapToCircle(bmap);
            //Bitmap of photo
            mImageV.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            //If failed to set bitmap of temp profile pic, use default
            mImageV.setImageDrawable(null);
            mImageV.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    //Save bitmap to profile
    private void saveSnap() {
        mImageV.buildDrawingCache();
        Bitmap bmap = mImageV.getDrawingCache();
        try {
            //attempt to save image to internal storage using bitmap
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //Save a temporary version of our profile photo in memory for when orientation changes
    //NOTE: Cannot use saveInstanceState's outState.putParcelable because bitmaps will cause overflow of small buffer
    private void saveSnapTemp() {
        mImageV.buildDrawingCache();
        Bitmap bmap = mImageV.getDrawingCache();
        try {
            //attempt to save image to internal storage using bitmap
            FileOutputStream fos = openFileOutput(getString(R.string.temp_profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //Function to begin cropping action
    private void beginCrop(Uri source) {
        // If we have a file to store our image in, continue
        if (mPhotoFile != null) {
            //Load image destination
            Uri destination = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID,
                    mPhotoFile);
            //Log for debugging
            Log.d("URI: ", destination.toString());
            //Crop our photo
            Crop.of(source, destination).asSquare().start(this);
        } else{
            //If we loaded the file from the image gallery i.e photoFile = null
            //but the source !=null, continue
            if (source != null){
                Uri destination = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID,
                        mPhotoFile);
                //LOG URI for debugging
                Log.d("URI: ", destination.toString());
                //Crop our file
                Crop.of(source, destination).asSquare().start(this);
            }
        }
    }

    //Do the actual cropping
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmap = bitmapToCircle(bitmap);
                mImageV.setImageBitmap(bitmap);
                saveSnapTemp();
            } catch (Exception e) {
                Log.d("Error", "error");
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean saveProfile(){
        saveSnap();

        mEditName.setError(null);
        mEditEmail.setError(null);
        mEditPassword.setError(null);

        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();

        //Need to check if password has been changed to launch appropriate intent after save
        if(!password.equals(initialPassword)){
            passwordChanged = true;
        }

        String phone = mEditPhoneNumber.getText().toString();
        String major = mMajor.getText().toString();
        String class_year = mClassYear.getText().toString();

        boolean canceled = false;
        int gender_selected = -1;
        boolean malechecked =  mMale.isChecked();
        boolean femalechecked =  mFemale.isChecked();

        if (malechecked){
            gender_selected = 1;

        } else if (femalechecked){
            gender_selected = 0;
        }

        if (TextUtils.isEmpty(name)){
            mEditName.setError("Name is required to register.");
            mEditName.requestFocus();
            canceled = true;
        }

        if (TextUtils.isEmpty(password)){
            mEditPassword.setError("Password is required to register.");
            mEditPassword.requestFocus();
            canceled = true;

        } else if(password.length() < 6){
            mEditPassword.setError("This password doesn't meet the minimum requirements. Must be" +
                    "longer than 5 characters.");
            mEditPassword.requestFocus();
            canceled = true;
        }

        if(TextUtils.isEmpty(email)){
            mEditEmail.setError("This field is required");
            mEditEmail.requestFocus();
            canceled = true;

        } else if (email.contains("@") == false){
            mEditEmail.setError("Email is not valid");
            mEditEmail.requestFocus();
            canceled = true;
        }

        if(gender_selected == -1){
            mRadioGenderGroup.requestFocus();
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show();
            canceled = true;
        }

        if (TextUtils.isEmpty(phone)){
            mEditPhoneNumber.setError("Phone field is required");
            mEditPhoneNumber.requestFocus();
            canceled = true;
        } else if(!TextUtils.isDigitsOnly(phone)){
            mEditPhoneNumber.setError("Phone number can only contain numbers.");
            mEditPhoneNumber.requestFocus();
            canceled = true;

        }

        if (TextUtils.isEmpty(class_year)){
            mClassYear.setError("Class Year field is required");
            mClassYear.requestFocus();
            canceled = true;
        } else if(!TextUtils.isDigitsOnly(class_year)){
            mClassYear.setError("Class Year field must be digits");
            mClassYear.requestFocus();
            canceled = true;

        }

        if(TextUtils.isEmpty(major)){
            mMajor.setError("Major field is required");
            mMajor.requestFocus();
            canceled = true;
        }

        if (canceled){
            return true;
        }else{
            //Here we take our profile and save it to the ProfilePreferences
            mProfilePreference.clearProfilePreferences();
            mProfilePreference.setProfileName(name);
            mProfilePreference.setProfileEmail(email);
            mProfilePreference.setProfilePassword(password);
            mProfilePreference.setProfileGender(gender_selected);
            mProfilePreference.setProfilePhone(phone);
            mProfilePreference.setProfileMajor(major);
            mProfilePreference.setProfileClassYear(class_year);
            mProfilePreference.ProfilePictureCommit();
            return false;
        }
    }

    private File createImageFile() throws IOException {
        //Use a timestamp to create a unique image file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
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

    public static Bitmap bitmapToCircle(Bitmap bMap) {
        //Get our dimensions
        int h = bMap.getHeight();
        int w = bMap.getWidth();

        //Create a new bitmap with each pixel stored on 4 bytes
        //For documentation see: https://developer.android.com/reference/android/graphics/Bitmap.Config
        Bitmap outputBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.	ARGB_8888);

        Path p = new Path();
        //Add our circle and orient it counter clockwise for closed shape
        //For documentation see: https://developer.android.com/reference/android/graphics/Path.Direction
        p.addCircle((float)(w / 2), (float)(h / 2), (float)Math.min(w, (h / 2)), Path.Direction.CCW);

        //Create new canvas to perform clipped with our circular path
        Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(p);
        //Draw our original bitmap onto our clipped canvas
        canvas.drawBitmap(bMap, 0, 0, null);
        return outputBitmap;
    }

    private void loadProfile(){
        try{
            mEditName.setText(mProfilePreference.getProfileName());
            mEditEmail.setText(mProfilePreference.getProfileEmail());
            if (mProfilePreference.getProfileGender() == 0) {
                mRadioGenderGroup.check(R.id.radioGenderF);
            } else if (mProfilePreference.getProfileGender() == 1) {
                mRadioGenderGroup.check(R.id.radioGenderM);
            }

            mEditPassword.setText(mProfilePreference.getProfilePassword());
            mEditPhoneNumber.setText(mProfilePreference.getProfilePhone());
            mMajor.setText(mProfilePreference.getProfileMajor());
            mClassYear.setText(mProfilePreference.getProfileClassYear());

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(DEBUG_TAG,"Register Profile onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "Register Profile onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "Register Profile onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "Register onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "Register onDestroy");
    }
}
