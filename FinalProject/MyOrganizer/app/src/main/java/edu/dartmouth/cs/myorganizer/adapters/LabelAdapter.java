package edu.dartmouth.cs.myorganizer.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.dartmouth.cs.myorganizer.LabelActivity;
import edu.dartmouth.cs.myorganizer.R;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.MyViewHolder> {



    ArrayList<String> mLabels;
    Context context;

    public LabelAdapter(Context context,ArrayList<String> labels) {
        this.context = context;


        this.mLabels = labels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_labels, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items

        holder.labels.setText(mLabels.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open another activity on item click
                Intent intent = new Intent(context, LabelActivity.class);
                intent.putExtra("label", position); // put image data in Intent

                context.startActivity(intent); // start Intent
            }
        });

    }


    @Override
    public int getItemCount() {
        return mLabels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView labels;


        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            labels = (TextView) itemView.findViewById(R.id.labels);

        }
    }
}

