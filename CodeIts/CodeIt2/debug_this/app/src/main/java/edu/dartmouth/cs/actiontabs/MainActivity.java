package edu.dartmouth.cs.actiontabs;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        // Define TabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
		tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
		fragments.add(new ChatFragment());
        fragments.add(new FindFragment());
        fragments.add(new MeetFragment());
        fragments.add(new PartyFragment());

        // use FragmentPagerAdapter to bind the TabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter =new ActionTabsViewPagerAdapter(getSupportFragmentManager(),
                fragments);
        // add the PagerAdapter to the viewPager
        viewPager.setAdapter(myViewPageAdapter);

        // The one-stop shop for setting up this TabLayout with a ViewPager.
		tabLayout.setupWithViewPager(viewPager);
		// Fixed tabs display all tabs concurrently
		tabLayout.setTabMode(TabLayout.MODE_FIXED);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_search:
			Toast.makeText(this, getString(R.string.ui_menu_search),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_send:
			Toast.makeText(this, getString(R.string.ui_menu_send),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_add:
			Toast.makeText(this, getString(R.string.ui_menu_add),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_share:
			Toast.makeText(this, getString(R.string.ui_menu_share),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_feedback:
			Toast.makeText(this, getString(R.string.ui_menu_feedback),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_about:
			Toast.makeText(this, getString(R.string.ui_menu_about),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_quit:
			Toast.makeText(this, getString(R.string.ui_menu_quit),
					Toast.LENGTH_SHORT).show();
			finish(); // close the activity
			return true;
		}
		return false;
	}



}