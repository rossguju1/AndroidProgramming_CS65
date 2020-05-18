package edu.dartmouth.cs.myruns4.adapters;

//import android.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

// Implementation of PagerAdapter that represents each page as a Fragment that is persistently
// kept in the fragment manager as long as the user can return to the page.
// This version of the pager is best for use when there are a handful of typically more static
// fragments to be paged through, such as a set of tabs. The fragment of each page the user
// visits will be kept in memory.

public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = ActionTabsViewPagerAdapter.class.getSimpleName();;
    private ArrayList<Fragment> fragments;

    public static final int START = 0;
    public static final int HISTORY = 1;
    public static final String UI_TAB_START = "CHAT";
    public static final String UI_TAB_HISTORY = "FIND";

    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    // Return the Fragment associated with a specified position.
    public Fragment getItem(int pos){
        Log.d(TAG, "getItem " + "position" + pos);
        return fragments.get(pos);
    }

    // Return the number of views available
    public int getCount(){
        Log.d(TAG, "getCount " + "size " + fragments.size());
        return fragments.size();
    }

    // This method may be called by the ViewPager to obtain a title string
    // to describe the specified page
    public CharSequence getPageTitle(int position) {
        Log.d(TAG, "getPageTitle " + "position " + position);
        switch (position) {
            case START:
                return UI_TAB_START;
            case HISTORY:
                return UI_TAB_HISTORY;
            default:
                break;
        }
        return null;
    }

}
