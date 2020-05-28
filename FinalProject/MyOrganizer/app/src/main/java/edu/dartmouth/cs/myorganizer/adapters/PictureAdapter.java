package edu.dartmouth.cs.myorganizer.adapters;

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


public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {


    ArrayList<Uri> mmImages;
    ArrayList<String> mmText;
    Context context;

    public PictureAdapter(Context context,ArrayList<Uri> Images, ArrayList<String> Text) {
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

        Uri temp_im = mmImages.get(position);

        Log.d("Reading save: (Uri in Adapter)", "" + temp_im);



        File tempFile= new File(mmImages.get(position).getPath());
        Log.d("Picture Adapter  ", "URI TO FILE : " + tempFile);

        Bitmap bitmap = null;
        try {
           // bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), temp_im);
           bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), temp_im);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.image.setImageBitmap(bitmap);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String formattedDate = sdf.format(date);
        holder.date.setText(formattedDate);
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
        private TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            image = (ImageView) itemView.findViewById(R.id.image);
            date = (TextView) itemView.findViewById(R.id.date_text);

        }


    }
}
