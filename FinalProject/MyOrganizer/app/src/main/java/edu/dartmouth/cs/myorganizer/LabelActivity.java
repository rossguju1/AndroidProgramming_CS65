package edu.dartmouth.cs.myorganizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;

public class LabelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);




        TextView item1 =  findViewById(R.id.ui_sameple_image);

        item1.setText("Classified Images ");


    }
}
