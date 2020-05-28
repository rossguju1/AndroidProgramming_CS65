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
import edu.dartmouth.cs.myorganizer.adapters.ActionTabsViewPagerAdapter;
import edu.dartmouth.cs.myorganizer.adapters.PictureAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
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

import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;


// do this https://stackoverflow.com/questions/45239381/refresh-the-fragment-after-camera-intent
public class PictureGridFragment extends Fragment {
    private static final int PICK_PDF_FILE = 2;
    private static final int PICK_DIRECTORY = 3;
    private static final String DEBUG = "mainactivity_debug";
    private static final int LOAD_IMAGE = 77;;
    private static final int REQUEST_TAKE_PICTURE_FROM_CAMERA = 0;
    private static final int ALL_COMMENTS_LOADER_ID = 1;
    PictureEntry ex;
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
    private ImageView mImageView;

    ArrayList<String> images;

    public RecyclerView recyclerView;

    //Tab stuff
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;
    PictureAdapter mAdapter;

    public static ArrayList<String> mText;
    public static ArrayList<Uri> mImages;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grid, container, false);
        setHasOptionsMenu(true);
        mImages = new ArrayList<Uri>();
        mText = new ArrayList<String>();

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewGrid);
        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        mAdapter= new PictureAdapter(getContext(), mImages, mText);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter); // set the Adapter to RecyclerView

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void checkPermissions() {
        //Check for appropriate version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        //Check if permission has been granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
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
//                try {
//                    //Create file to save photo
//                    loadPhotoFile = createImageFile();
//                } catch (IOException ex) {
//                    // Error occurred while creating the File
//                    ex.printStackTrace();
//                }
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
        int curSize = mAdapter.getItemCount();



        Log.d(DEBUG, "curSize: " + curSize);


//        if (resultCode != Activity.RESULT_OK) {
//            Log.d(DEBUG, "onActivity Result not ok");
//            return;
//        }
        switch (requestCode) {


            case LOAD_IMAGE:
                //Log.d(DEBUG, "LOAD_IMAGE:");
                if (data.getData() == null){
                    return;
                }
                Uri picUri = data.getData();
                Log.d(DEBUG, "LOAD_IMAGE PicUri: " + picUri );
//                int takeFlags = data.getFlags();
//                takeFlags &= Intent.FLAG_GRANT_READ_URI_PERMISSION;
//                getContext().getContentResolver().takePersistableUriPermission(loadPhotoUri, takeFlags);
//               // BitmapUri bitmapUri = new BitmapUri(Uri.decode(loadPhotoUri.toString()), data.getFlags());


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


                final MyPicture entry = new MyPicture();
                final String temp_text;
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
                        int siz = mAdapter.getItemCount();
                        String text = firebaseVisionText.getText();
                        //temp_text = new String(text);
                        entry.setmImage(loadPhotoUri);
                        Log.d("Presave: ", "" + loadPhotoUri);
                        mImages.add(loadPhotoUri);
                        mText.add(text);
                        entry.setmText(text);
                        entry.setmLabel(-1);

                        PictureEntry pp = new PictureEntry(getContext());
                        pp.open();
                        pp.insertEntry(entry);
                        pp.close();

                        String upToNCharacters = text.substring(0, Math.min(text.length(), 30));

                        Log.d(DEBUG, "TEXT:    " + text);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.d(DEBUG, "onFailure");
                    }
                });





                Log.d(DEBUG, "PICK_IMAGE URI before save: " + loadPhotoUri);
                mAdapter.notifyItemInserted( mImages.size()-1);
               // mAdapter.notifyItemRangeInserted(curSize, 1);

                break;
            case REQUEST_TAKE_PICTURE_FROM_CAMERA:
                Log.d(DEBUG, "REQUEST_TAKE_PICTURE_FROM_CAMERA");

             if (cameraPhotoUri == null){
                 Log.d(DEBUG, "cameraPhotoUri == null");
                 return;
             }

                Log.d("Picture Frag", "(OLD)loadPhotoUri:  " + loadPhotoUri);
                Log.d("Picture Frag", "(OLD)loadPhotoFile:  " + loadPhotoFile);






//                FileInputStream fis = openFileInput(loadPhotoFile.getAbsolutePath());
//                Bitmap bmap = BitmapFactory.decodeStream(fis);

                final Bitmap rotatedBitmap = imageOrientationValidator(cameraPhotoFile);



                try {
                    //Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);

                    loadPhotoFile = createImageFile();
                    FileOutputStream fOut = new FileOutputStream(loadPhotoFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Set mImageCaptureUri so we can save the state if stop in lifecycle
                loadPhotoUri = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID,
                        loadPhotoFile);
                Log.d(DEBUG, "(NEW)loadPhotoUri:  " + loadPhotoUri);
                Log.d(DEBUG, "(NEW)loadPhotoFile:  " + loadPhotoFile);





                final MyPicture entry2 = new MyPicture();
                try {
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(rotatedBitmap);
                    FirebaseVisionTextRecognizer textRecognizer =
                            FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                    textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {

                            String text = firebaseVisionText.getText();

                            String upToNCharacters = text.substring(0, Math.min(text.length(), 30));
                            mImages.add(cameraPhotoUri);
                            entry2.setmImage(cameraPhotoUri);

                            Log.d("Presave Camera: ", "" + cameraPhotoUri);


                            mText.add(text);
                            entry2.setmText(text);


                            entry2.setmLabel(-1);

                            PictureEntry p = new PictureEntry(getContext());
                            p.open();
                            p.insertEntry(entry2);
                            p.close();


                            Log.d(DEBUG, "TEXT:    " + text);

                            mAdapter.notifyItemInserted( mImages.size()-1);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });



                } catch (Exception e) {

                }


                break;
            default:

                break;

        }




        mAdapter.notifyItemRangeInserted(curSize, 1);



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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
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
        ArrayList<MyPicture> temp = ex.getAllPictures();
        for (int i = 0; i < temp.size(); i ++){
            MyPicture t = temp.get(i);
            Uri b = Uri.parse(t.getmImage());
            String tex = t.getmText();
            Log.d(DEBUG, "TEXT: " + tex);
            Log.d(DEBUG, "URI TEXT:  " + b);

            // Uri tempy =  Uri.parse(b);
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),tempy);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            // Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            mImages.add(b);
            mText.add(tex);

        }

        ex.close();

        //mAdapter = new PictureAdapter(getContext(), mImages, mText);

        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyItemInserted(mImages.size() - 1);
       // mAdapter.notifyDataSetChanged();



    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG, "onResume()");
//        ex = new PictureEntry(getContext());
//        ex.open();
//        ArrayList<MyPicture> temp = ex.getAllPictures();
//        for (int i = 0; i < temp.size(); i ++){
//        MyPicture t = temp.get(i);
//        Uri b = Uri.parse(t.getmImage());
//        String tex = t.getmText();
//        Log.d(DEBUG, "TEXT: " + tex);
//        Log.d(DEBUG, "URI TEXT:  " + b);
//
//         // Uri tempy =  Uri.parse(b);
////            Bitmap bitmap = null;
////            try {
////                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),tempy);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//            // Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//        mImages.add(b);
//        mText.add(tex);
//
//        }
//
//        ex.close();
//
//        //mAdapter = new PictureAdapter(getContext(), mImages, mText);
//
//        recyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
//


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




    private void createCustomFile(String fileName) {
        File path = new File(getContext().getFilesDir(), "MyOrganizer" + File.separator + "Images");
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





}