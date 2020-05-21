package edu.dartmouth.cs.myorganizer;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_PDF_FILE = 2;
    private static final int PICK_DIRECTORY = 3;
    private static final String DEBUG = "mainactivity_debug";
    private static final int LOAD_IMAGE = 77;;
    private static final int REQUEST_TAKE_PICTURE_FROM_CAMERA = 0;
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    Uri cameraPhotoUri;
    Uri loadPhotoUri;
    Uri fileUri;
    Uri directoryUri;
    File cameraPhotoFile;
    File loadPhotoFile;
    File fileFile;
    File directoryFile;
    Bitmap rotatedBitmap;
    File myorganizerDir;

    ArrayList<String> images;

    public RecyclerView recyclerView;
    public static FilesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  Toolbar toolbar = findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        images = new  ArrayList<String>();
        images.add("Helllo");
        mAdapter = new FilesAdapter(this, images);

       // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    private void checkPermissions() {
        //Check for appropriate version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        //Check if permission has been granted
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        } else if (id == R.id.action_syncro) {

            return true;
        } else if (id == R.id.action_plus) {

            checkPermissions();
            displayDialog(AddfileFragmentFragment.LOAD_PHOTO_ITEM);
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayDialog(int id) {
        //Dialogue fragment for photo gallery photo selection
        DialogFragment fragment = AddfileFragmentFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .add(fragment, "file_added")
                .commit();
    }

    public void onPhotoPickerItemSelected(int item) {
    Log.d(DEBUG, "onPhotoPickerItemSelected");
        switch (item) {
            case AddfileFragmentFragment.TAKE_PHOTO_PHOTO_ITEM:
                // Explicit intent used to take photo
                Log.d("HERE", "");
                dispatchTakePictureIntent();

                break;

            case AddfileFragmentFragment.LOAD_PHOTO_ITEM:
                try {
                    //Create file to save photo
                    loadPhotoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                //Intent used when selecting from image gallery i.e picking image
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE);
            case AddfileFragmentFragment.OTHER_FILE_ITEM:
                Log.d("HERE", "");


            default:
                return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(DEBUG, "onActivityResult: result code " + resultCode);
        Log.d(DEBUG, "onActivityResult: requestcode " + requestCode);
        int curSize = mAdapter.getItemCount();
        Log.d(DEBUG, "curSize: " + curSize);
        Uri uri = null;
//        if (resultCode != Activity.RESULT_OK) {
//            Log.d(DEBUG, "onActivity Result not ok");
//            return;
//        }
        switch (requestCode) {
//https://developer.android.com/training/data-storage/shared/documents-files
            case (PICK_DIRECTORY):
                Log.d(DEBUG, "PICK_DIRECTORY");
                // The result data contains a URI for the document or directory that
                // the user selected.

//                if (data != null) {
//                    uri = data.getData();
//                    // Perform operations on the document using its URI.
//                }

                //do stuff
                break;
            case (PICK_PDF_FILE):
                // The result data contains a URI for the document or directory that
                // the user selected.

//                uri = data.getData();
                // Perform operations on the document using its URI.

                break;
            case LOAD_IMAGE:
                Log.d(DEBUG, "LOAD_IMAGE");
                loadPhotoUri = data.getData();

                images.add(loadPhotoUri.toString());
                Log.d(DEBUG, "PICK_IMAGE URI before save: " + loadPhotoUri);

                SharedPreferences mPrefs1 = getSharedPreferences("uri",0);
                SharedPreferences.Editor mEditor1 = mPrefs1.edit();
                mEditor1.putString("imageURI", loadPhotoUri.toString()).commit();

                SharedPreferences settings = getSharedPreferences("uri", 0);
                String imageUriString = settings.getString("imageURI", null);
                Uri imageUri = Uri.parse(imageUriString);

                Log.d(DEBUG, "PICK_IMAGE: uri after saving: " + imageUri);
                //ImageView imageView;
                //imageView.setImageBitmap(imageBitmap);

                break;
            case REQUEST_TAKE_PICTURE_FROM_CAMERA:
                Log.d(DEBUG, "REQUEST_TAKE_PICTURE_FROM_CAMERA: loadPhotofile: " + loadPhotoFile);

                Bitmap rotatedBitmap = imageOrientationValidator(cameraPhotoFile);
                try {
                    cameraPhotoFile = createImageFile();
                    FileOutputStream fOut = new FileOutputStream(cameraPhotoFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Set mImageCaptureUri so we can save the state if stop in lifecycle
                cameraPhotoUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID,
                        cameraPhotoFile);

                images.add(cameraPhotoUri.toString());
                Log.d(DEBUG, "uri before saving: " + cameraPhotoUri);
                SharedPreferences mPrefs = getSharedPreferences("uri",0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("imageURI", cameraPhotoUri.toString()).commit();

                SharedPreferences setting = getSharedPreferences("uri", 0);
                String imageUriStrings = setting.getString("imageURI", null);
                Uri imageUris = Uri.parse(imageUriStrings);
                Log.d(DEBUG, "uri after saving: " + imageUris);
                break;


        }

        mAdapter.notifyItemRangeInserted(curSize, 1);

       // recyclerView.setAdapter(mAdapter);
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Provide read access to files and sub-directories in the user-selected
        // directory.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
// Check for the freshest data.
        getContentResolver().takePersistableUriPermission(uriToLoad, takeFlags);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, PICK_DIRECTORY);
    }


    private void dispatchTakePictureIntent() {

        // Explicit intent used to take photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setType("/image/*");

        try {
            // File to store our image
            cameraPhotoFile = createImageFile();
        } catch (IOException ex) {
            // Failed to create file, error occured
            ex.printStackTrace();
        }

        //Prevent error if failed to create file
        if (cameraPhotoFile != null) {
            cameraPhotoUri = FileProvider.getUriForFile(this,
                    "edu.dartmouth.cs.myorganizer",
                    cameraPhotoFile);
            //Handle extra output case (see Android documentation)
            Log.d(DEBUG, "cameraPhotoUri:   " + cameraPhotoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
        }

        try {
            // Take photo, use tag to ID action in onActivityResult() on return
            startActivityForResult(intent, REQUEST_TAKE_PICTURE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
//
//
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            cameraPhotoFile = null;
//            try {
//                cameraPhotoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (cameraPhotoFile != null) {
//                cameraPhotoUri = FileProvider.getUriForFile(this,
//                        BuildConfig.APPLICATION_ID,
//                        cameraPhotoFile);
//                Log.d(DEBUG, "cameraPhotoFile: " + cameraPhotoUri.toString());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
//                try {
//                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE_FROM_CAMERA);
//
//                } catch (Exception e){
//                    Log.d(DEBUG, "Exception: "+String.valueOf(e));
//                }
//            }
//        }
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

    private File createImageFile_v2() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private Bitmap imageOrientationValidator(File photoFile) {
        ExifInterface ei;
        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FileProvider.getUriForFile(this,
                    "edu.dartmouth.cs.myorganizer",
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

    private void createCustomFile(String fileName) {
        File path = new File(getApplicationContext().getFilesDir(), "MyOrganizer" + File.separator + "Images");
        if (!path.exists()) {
            path.mkdirs();
        }
        File outFile = new File(path, fileName + ".jpeg");
        //now we can create FileOutputStream and write something to file
// other way:


//        myorganizerDir= new File(Environment.getExternalStorageDirectory() +
//                File.separator + "myorganizerDir");
//        boolean success = true;
//        if (!myorganizerDir.exists()) {
//            success = myorganizerDir.mkdirs();
//        }
//        if (success) {
//            // Do something on success
//        } else {
//            // Do something else on failure
//        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG, "onResume");


    }


}