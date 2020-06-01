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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import edu.dartmouth.cs.myorganizer.database.FuegoBaseEntry;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;
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
    private AsyncDelete delete_task = null;
    ArrayList<String> images;

    public RecyclerView recyclerView;

    public SharedPreferences sharedPreferences;




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
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //mDatabase = FirebaseDatabase.getInstance().getReference();


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

        if (LoadInt() >= 0){

            viewPager.setCurrentItem(LoadInt());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_grid:
                            SaveInt(0);
                            viewPager.setCurrentItem(0);

                            break;
                        case R.id.navigation_labels:
                            SaveInt(1);
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

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG, "onPause");


    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.d(DEBUG, "delete selected");
               // int prev = mInput.size();
                final long result_id =data.getLongExtra("result", -1);
                int result_pos=data.getIntExtra("pos", -1);
                Log.d(DEBUG, "delete selected: result_id and result_pos :" + result_id + "  " + result_pos);
                //mInput.remove(result_pos);
                // mAdapter.notifyItemRangeRemoved(prev, 1);
                delete_task=new AsyncDelete(result_id);
                delete_task.execute();

                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();

                String mUserId = mFirebaseUser.getUid();
                Log.d(DEBUG, "indelete userId: " + mUserId);
                mDatabase = FirebaseDatabase.getInstance().getReference("user_" + mUserId);
                mDatabase.child("picture_entries").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //getting userinfo
                            FuegoBaseEntry entry = postSnapshot.getValue(FuegoBaseEntry.class);
                            long current_id = Long.parseLong(entry.getId());
                            Log.d(DEBUG, "Found item: " + current_id);
                            if (current_id == result_id){
                                Log.d(DEBUG, "Found item: " + current_id);

                                postSnapshot.getRef().removeValue();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // mAdapter.notifyDataSetChanged();
               // mAdapter.notifyItemRangeRemoved(prev, 1);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_signout) {
            Log.d(DEBUG, "signout Clicked");
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseAuth.signOut();
            loadLogInView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadLogInView() {
        Intent intent = new Intent(this, LoginFireBaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
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
        viewPager.setCurrentItem(LoadInt());


    }
    public void SaveInt(int value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("key", value);
        editor.commit();
    }
    public int LoadInt(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       int current_page = sharedPreferences.getInt("key", -1);
        return current_page;
    }

    class AsyncDelete extends AsyncTask<Void, String, Void> {
        int pos;
        private long delete_id;
        AsyncDelete(long id){
            this.delete_id = id;

        }
        @Override
        protected Void doInBackground(Void... unused) {

            Log.d(DEBUG, "USER HIT DELETE! and wants to Delete: " + delete_id);


            PictureEntry mEntry = new PictureEntry(getApplicationContext());
            mEntry.open();

            int ret = mEntry.deletePicture(delete_id);
            mEntry.close();
            if (ret>0){
                Log.d("DEBUG", "DeleteWorked and removed: " + delete_id);

            } else {
                Log.d("DEBUG", "Delete Failed to remove: " + delete_id);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {
                //mAdapter.add(name[0]);
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            // Log.d(DEBUG, "Delete Done:   " + pos);
            delete_task = null;




        }
    }

}
