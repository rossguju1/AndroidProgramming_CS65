package edu.dartmouth.cs.myorganizer.adapters;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import android.view.LayoutInflater;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.TextActivity;
import edu.dartmouth.cs.myorganizer.database.MyPicture;


public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    ArrayList<MyPicture> mInput;

    Context context;

    public PictureAdapter(Context context, ArrayList<MyPicture> input) {
        this.context = context;
        this.mInput = input;

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
        Log.d("Picture Adapter", "adapter position: " + holder.getAdapterPosition());
        Log.d("Picture Adapter", "position: " + position);


        MyPicture res = mInput.get(holder.getAdapterPosition());

        Uri resUri = Uri.parse(res.getmImage());


        File tempFile= new File(resUri.getPath());
        Log.d("Picture Adapter  ", "URI TO FILE : " + tempFile);

        Bitmap bitmap = null;
        try {
           bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.image.setImageBitmap(bitmap);



        holder.date.setText(res.getmDate());



        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Label Adapter:", "clicked on image: pos "+ position + "ID: " + mInput.get(position).getId());
                Intent intent = new Intent(v.getContext(), TextActivity.class);
                intent.putExtra("text", mInput.get(position).getmText()); // put image data in Intent
                intent.putExtra("pos", position);
                intent.putExtra("id", mInput.get(position).getId());
                ((Activity) context).startActivityForResult(intent, 1);
                //context.startActivity(intent); // start Intent

            }

        });


    }


    @Override
    public int getItemCount() {
        return mInput.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        // init the item view's
        private ImageView image;
        private TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            image = (ImageView) itemView.findViewById(R.id.image);
            date = (TextView) itemView.findViewById(R.id.date_text);

        }


    }
}
