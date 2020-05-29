package edu.dartmouth.cs.myorganizer;

import android.Manifest;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.dartmouth.cs.myorganizer.adapters.ActionTabsViewPagerAdapter;
import edu.dartmouth.cs.myorganizer.fragments.AddfileFragmentFragment;
import edu.dartmouth.cs.myorganizer.fragments.LabelsFragment;
import edu.dartmouth.cs.myorganizer.fragments.PictureGridFragment;

public class MainActivity extends AppCompatActivity{
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
    private ImageView mImageView;

    ArrayList<String> images;

    public RecyclerView recyclerView;

    //Tab stuff
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        checkPermissions();

        // Get viewPager instance
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // create a fragment list in order.
        fragments = new ArrayList<>();
        fragments.add(new PictureGridFragment());
        fragments.add(new LabelsFragment());

        // use FragmentPagerAdapter to bind the TabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter = new ActionTabsViewPagerAdapter(getSupportFragmentManager(),
                fragments);
        // add the PagerAdapter to the viewPager
        viewPager.setAdapter(myViewPageAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_grid:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.navigation_labels:
                            viewPager.setCurrentItem(1);
                            break;
                    }
                    return false;
                }
            }
        );

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
           Log.d(DEBUG, "mFirebaseUser == null");
        } else {
            mDatabase.setValue("Hello, World!");
        }
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

//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//
//            return true;
//        } else if (id == R.id.action_syncro) {
//
//            return true;
//        } else if (id == R.id.action_plus) {
//
//            checkPermissions();
//            displayDialog(AddfileFragmentFragment.LOAD_PHOTO_ITEM);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
    public void displayDialog(int id) {
        //Dialogue fragment for photo gallery photo selection
        DialogFragment fragment = AddfileFragmentFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .add(fragment, "file_added")
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG, "onResume");
    }

}
