package edu.dartmouth.cs.myruns2;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayEntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Exercise Entry");




    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.manual_entry_menu, menu);
//        //Set the appropriate button title depending on navigation context
//        if(getIntent().getStringExtra(MANUAL_INTENT_FROM).equals("start_tab")){
//            current_tab = 0;
//            menu.getItem(0).setTitle("SAVE");
//        }else if (getIntent().getStringExtra(MANUAL_INTENT_FROM).equals("history_tab")){
//            current_tab = 1;
//            menu.getItem(0).setTitle("DELETE");
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

}
