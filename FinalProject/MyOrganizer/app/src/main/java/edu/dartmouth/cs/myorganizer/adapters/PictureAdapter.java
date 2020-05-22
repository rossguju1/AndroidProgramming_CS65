package edu.dartmouth.cs.myorganizer.adapters;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import android.view.LayoutInflater;
import android.widget.TextSwitcher;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.TextActivity;


public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {


    ArrayList<Bitmap> mmImages;
    ArrayList<String> mmText;
    Context context;

    public PictureAdapter(Context context,ArrayList<Bitmap> Images, ArrayList<String> Text) {
        this.context = context;

        this.mmImages = Images;
        this.mmText = Text;
    }

    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_images, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PictureAdapter.ViewHolder holder, final int position) {
        // set the data in items

        holder.image.setImageBitmap(mmImages.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Label Adapter:", "clicked on image: pos "+ position);
                Intent intent = new Intent(v.getContext(), TextActivity.class);
                intent.putExtra("text", mmText.get(position)); // put image data in Intent
                context.startActivity(intent); // start Intent

            }

        });


    }


    @Override
    public int getItemCount() {
        return mmImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        // init the item view's
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            image = (ImageView) itemView.findViewById(R.id.image);

        }


    }
}
