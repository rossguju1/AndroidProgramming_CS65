package edu.dartmouth.cs.myruns2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


public class MainMyRunsActivity extends AppCompatActivity {


    private FloatingActionButton mStartButton;
    private Spinner mInput;
    private Spinner mActivity;
    private String input = "";
    private String activity = "";

    private static final String DEBUG_TAG = "<<<debugger>>>";
    private ActionBar actionBar;

    private static final String TAG = "from_main_activity";


    //Tab stuff
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main Activity");
        setContentView(R.layout.main_activity);

        // Get viewPager instance
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(new StartFragment());
        fragments.add(new HistoryFragment());


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
                            case R.id.navigation_start:

                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_history:

                                viewPager.setCurrentItem(1);
                                break;
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_settings_menu) {

            // Launch our settings activity that serves as a wrapper for the settings fragment
            Intent intent = new Intent(MainMyRunsActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        //https://stackoverflow.com/questions/9664108/how-to-finish-parent-activity-from-child-activity
        else if (id == R.id.main_edit_profile_menu) {
            Intent intent = new Intent(MainMyRunsActivity.this, RegisterProfileActivity.class);

            intent.putExtra(RegisterProfileActivity.INTENT_FROM, TAG);
            startActivity(intent);


        } else if (id == android.R.id.home) {         //On home button click
            //finish();
            //return true;
            //.onBackPressed();
            // ((AppCompatActivity)getActivity()).onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.root_preferences);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK)
//            return;
//
//    }
}
