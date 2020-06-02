package edu.dartmouth.cs.myorganizer.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
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


    int BiologyColor = Color.parseColor("#4BAF4F");
    int MathColor = Color.parseColor("#F44335");
    int HistoryColor = Color.parseColor("#2196F3");
    int PhysicsColor = Color.parseColor("#009688");
    int ThermodynamicsColor = Color.parseColor("#FF9800");
    int SmartphoneColor = Color.parseColor("#4BAF4F");

    ArrayList<String> mLabels;
    Context context;

    public LabelAdapter(Context context,ArrayList<String> labels) {
        this.context = context;
        this.mLabels = labels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_labels, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items

        holder.labels.setText(mLabels.get(position));
        holder.labels.setTextColor(Color.WHITE);
        if(position == 0){
            holder.labels.setBackgroundColor(BiologyColor);
        } else if (position == 1) {
            holder.labels.setBackgroundColor(MathColor);
        } else if (position == 2) {
            holder.labels.setBackgroundColor(HistoryColor);
        } else if (position == 3) {
            holder.labels.setBackgroundColor(PhysicsColor);
        } else if (position == 4) {
            holder.labels.setBackgroundColor(ThermodynamicsColor);
        } else if (position == 5) {
            holder.labels.setBackgroundColor(SmartphoneColor);
        }

        //Set our appropriate img/icon
        String uri = "@drawable/" + mLabels.get(position).toLowerCase() + "_fa";  // where myresource (without the extension) is the file
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable res = context.getResources().getDrawable(imageResource);
        holder.labels.setCompoundDrawablesWithIntrinsicBounds(res, null, null, null);
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open another activity on item click
                Intent intent = new Intent(context, LabelActivity.class);
                intent.putExtra("label", position); // put image data in Intent
           //    ((Activity) context).startActivityForResult(intent, 1);
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

