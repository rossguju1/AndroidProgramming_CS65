package edu.dartmouth.cs.actiontabs;

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

    public static final int CHAT = 0;
    public static final int FIND = 1;
    public static final int MEET = 2;
    public static final int PARTY = 3;
    public static final String UI_TAB_CHAT = "CHAT";
    public static final String UI_TAB_FIND = "FIND";
    public static final String UI_TAB_MEET = "MEET";
    public static final String UI_TAB_PARTY = "PART";

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
            case CHAT:
                return UI_TAB_CHAT;
            case FIND:
                return UI_TAB_FIND;
            case MEET:
                return UI_TAB_MEET;
            case PARTY:
                return UI_TAB_PARTY;
            default:
                break;
        }
        return null;
    }
}
