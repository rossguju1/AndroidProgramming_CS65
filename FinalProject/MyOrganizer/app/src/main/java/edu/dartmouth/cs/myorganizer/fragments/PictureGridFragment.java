package edu.dartmouth.cs.myorganizer.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.dartmouth.cs.myorganizer.BuildConfig;
import edu.dartmouth.cs.myorganizer.Globals;
import edu.dartmouth.cs.myorganizer.adapters.ActionTabsViewPagerAdapter;
import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;
import edu.dartmouth.cs.myorganizer.database.FuegoBaseEntry;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.database.AsyncPictureLoader;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;


// do this https://stackoverflow.com/questions/45239381/refresh-the-fragment-after-camera-intent
public class PictureGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<MyPicture>> {
    private static final int PICK_PDF_FILE = 2;
    private static final int PICK_DIRECTORY = 3;
    private static final String DEBUG = "PictureGridFragment";
    private static final int LOAD_IMAGE = 77;;
    private static final int REQUEST_TAKE_PICTURE_FROM_CAMERA = 0;
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    public SharedPreferences sharedPreferences;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    PictureEntry ex;
    Uri cameraPhotoUri;
    Uri loadPhotoUri;

    File cameraPhotoFile;
    File loadPhotoFile;

    Bitmap rotatedBitmap;

    public long inserted_id;

    public RecyclerView recyclerView;


    private PictureAdapter mAdapter;
    private static ArrayList<MyPicture> mInput;


    private String mUserId;


    private AsyncInsert task = null;
   // private AsyncDelete delete_task = null;
    private int pic_result;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grid, container, false);
        setHasOptionsMenu(true);
        Log.d(DEBUG, "onCreateView()");



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUserId = mFirebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference("user_" + mUserId);

        //mInput = new ArrayList<MyPicture>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewGrid);
        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
       // mAdapter= new PictureAdapter(getContext(),  mInput);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(mAdapter); // set the Adapter to RecyclerView

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            Log.d(DEBUG, "signout Clicked");

            return true;
        } else if (id == R.id.action_syncro) {

            return true;
        } else if (id == R.id.action_camera) {
            onPhotoPickerItemSelected(REQUEST_TAKE_PICTURE_FROM_CAMERA);
        } else if(id == R.id.action_gallery){
            onPhotoPickerItemSelected(AddfileFragmentFragment.LOAD_PHOTO_ITEM);
        }

        return super.onOptionsItemSelected(item);
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

                //Intent used when selecting from image gallery i.e picking image
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //i.putExtra(MediaStore.EXTRA_OUTPUT, loadPhotoFile);
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
        int prev_adapter_size = mAdapter.getItemCount();
        int prev_minput_size = mInput.size();



        Log.d(DEBUG, "previous adapter size: " + prev_adapter_size);
        Log.d(DEBUG, "previous minput size: " + prev_minput_size);



//        if (resultCode != Activity.RESULT_OK) {
//            Log.d(DEBUG, "onActivity Result not ok");
//            return;
//        }
        switch (requestCode) {


            case LOAD_IMAGE:
                //Log.d(DEBUG, "LOAD_IMAGE:");
                try{
                if (data.getData() == null){
                    return;
                }}catch (Exception e){

                    return;
                }

                Uri picUri = data.getData();
                Log.d(DEBUG, "LOAD_IMAGE PicUri: " + picUri );
                pic_result = requestCode;

                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);

                    loadPhotoFile = createImageFile();
                    FileOutputStream fOut = new FileOutputStream(loadPhotoFile);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Set mImageCaptureUri so we can save the state if stop in lifecycle
                loadPhotoUri = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID,
                        loadPhotoFile);
                Log.d("Picture Frag", "loadPhotoUri:  " + loadPhotoUri);
                Log.d("Picture Frag", "loadPhotoFile:  " + loadPhotoFile);

                task = new AsyncInsert();
                task.execute();

                break;
            case REQUEST_TAKE_PICTURE_FROM_CAMERA:
                Log.d(DEBUG, "REQUEST_TAKE_PICTURE_FROM_CAMERA");
                try {
                 if (cameraPhotoUri == null){
                     Log.d(DEBUG, "cameraPhotoUri == null");
                     return;
                 }} catch (Exception e){
                    return;
                }

                Log.d("Picture Frag", "(OLD)loadPhotoUri:  " + cameraPhotoFile);
                Log.d("Picture Frag", "(OLD)loadPhotoFile:  " + cameraPhotoFile);

                pic_result = requestCode;



//                FileInputStream fis = openFileInput(loadPhotoFile.getAbsolutePath());
//                Bitmap bmap = BitmapFactory.decodeStream(fis);

                final Bitmap rotatedBitmap = imageOrientationValidator(cameraPhotoFile);
                if (rotatedBitmap == null){
                    return;
                }


                try {
                    //Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);

                    cameraPhotoFile = createImageFile();
                    FileOutputStream fOut = new FileOutputStream(cameraPhotoFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Set mImageCaptureUri so we can save the state if stop in lifecycle
                cameraPhotoUri = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID,
                        cameraPhotoFile);
                Log.d(DEBUG, "(NEW)loadPhotoUri:  " + cameraPhotoUri);
                Log.d(DEBUG, "(NEW)loadPhotoFile:  " + cameraPhotoFile);



                task = new AsyncInsert();
                task.execute();



                break;
            default:

                break;

        }





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
            cameraPhotoUri = FileProvider.getUriForFile(getContext(),
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

    }

    private File createImageFile() throws IOException {
        //Use a timestamp to create a unique image file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        String currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private Bitmap imageOrientationValidator(File photoFile) {
        ExifInterface ei;
        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), FileProvider.getUriForFile(getContext(),
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



    @Override
    public void onStart() {
        super.onStart();
        Log.d(DEBUG, "onStart");

        ex = new PictureEntry(getContext());
        ex.open();
        LoaderManager.getInstance(this).initLoader(ALL_COMMENTS_LOADER_ID, null, this);


    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG, "onResume()");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG, "onDestroy");

    }



    @NonNull
    @Override
    public Loader<ArrayList<MyPicture>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(DEBUG, "onCreateLoader: Thread ID: " + Thread.currentThread().getId());
        if (id == ALL_COMMENTS_LOADER_ID){
            return new AsyncPictureLoader(getContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MyPicture>> loader, ArrayList<MyPicture> data) {
        Log.d(DEBUG, "onLoadFinished: Thread ID: " + Thread.currentThread().getId());
        if (loader.getId() == ALL_COMMENTS_LOADER_ID) {

            Log.d(DEBUG, "onLoadFinished: dataSize: " + data.size());
            //ArrayList<MyPicture> mInput =  (ArrayList<MyPicture>) data.clone();
            mInput = data;
            Log.d(DEBUG, "mInput Size in loader:" + mInput.size());
            Log.d(DEBUG, "Creating adapter");
            mAdapter = new PictureAdapter(getContext(), mInput);
            recyclerView.setAdapter(mAdapter);


            PictureEntry mEntry = new PictureEntry(getActivity());
            mEntry.close();


        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<MyPicture>> loader) {
        Log.d(DEBUG, "onLoaderReset: Thread ID: " + Thread.currentThread().getId());

    }






    class AsyncInsert extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(DEBUG, "AsyncInsert doInBackground()");

            final MyPicture entry = new MyPicture();


            if(pic_result == 0){


                Bitmap mBitmap = null;
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cameraPhotoUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                FirebaseVisionImage _image = FirebaseVisionImage.fromBitmap(mBitmap);
                FirebaseVisionTextRecognizer _textRecognizer =
                        FirebaseVision.getInstance().getOnDeviceTextRecognizer();

                _textRecognizer.processImage(_image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String text = firebaseVisionText.getText();
                        int prev = mInput.size();
                        entry.setmImage(cameraPhotoUri);
                        entry.setmText(text);

                        Random random = new Random();
                        int randomInteger = random.nextInt(4);
                        Log.d(DEBUG, "label number: " + randomInteger);
                        entry.setmLabel(randomInteger);

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                        String formattedDate = sdf.format(date);
                        entry.setmDate(formattedDate);
                        entry.setmSynced(0);
                        mInput.add(entry);


                        mAdapter.notifyItemRangeInserted(prev, 1);

                        PictureEntry pp = new PictureEntry(getContext());
                        pp.printPicture(entry);
                        pp.open();
                        inserted_id = pp.insertEntry(entry);
                        pp.close();

                        insertPictureFuegoBase(entry);

                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(DEBUG, "onFailure");
                    }
                });
            } else if (pic_result == 77){

                Bitmap mBitmap = null;
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), loadPhotoUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                FirebaseVisionImage _image = FirebaseVisionImage.fromBitmap(mBitmap);
                FirebaseVisionTextRecognizer _textRecognizer =
                        FirebaseVision.getInstance().getOnDeviceTextRecognizer();

                _textRecognizer.processImage(_image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String text = firebaseVisionText.getText();
                        int prev = mInput.size();
                        entry.setmImage(loadPhotoUri);
                        entry.setmText(text);
                        Random random = new Random();
                        int randomInteger = random.nextInt(4);
                        Log.d(DEBUG, "label number: " + randomInteger);
                        entry.setmLabel(randomInteger);
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                        String formattedDate = sdf.format(date);
                        entry.setmDate(formattedDate);
                        entry.setmSynced(0);
                        mInput.add(entry);
                        mAdapter.notifyItemRangeInserted(prev, 1);

                        PictureEntry pp = new PictureEntry(getContext());
                        pp.printPicture(entry);
                        pp.open();
                        pp.insertEntry(entry);
                        pp.close();

                        insertPictureFuegoBase(entry);

                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(DEBUG, "onFailure");
                    }
                });


            } else {
              Log.d(DEBUG, "Insert failed because of bad URI");
            }


            return null;
        }


        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
                // ((MainActivity) context).onResult(result);
                //mAdapter.add(name[0]);

            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(DEBUG, "INSERT THREAD DONE");
            task = null;
            SaveLabelState(-1);

        }
    }

    public void SaveLabelState(int value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("organized", value);
        editor.commit();
    }

    public void insertPictureFuegoBase(MyPicture entry){

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String email = sharedPreferences.getString("email", "");



        Log.d(DEBUG, "Email: " + email);

//        public String id;
//        public String imageUri;
//        public String text;
//        public String label;
//        public String date;
        FuegoBaseEntry FuegoEntry = new FuegoBaseEntry(email, String.valueOf(entry.getId()), entry.getmImage().toString(), entry.getmText(), String.valueOf(entry.getmLabel()), entry.getmDate(), String.valueOf(entry.getmSynced()));


       String ts= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Log.d(DEBUG, "TimeStamp: " + ts);

        mDatabase.child("picture_entries").push().setValue(FuegoEntry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(DEBUG, "successfully inserted entry");
                // Write was successful!
                // ...
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(DEBUG, "Failed to inserted entry");

                // Write failed
                // ...
            }});


    }

}