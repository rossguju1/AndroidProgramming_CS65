package edu.dartmouth.cs.myorganizer.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.view.LayoutInflater;
import android.widget.RelativeLayout;
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
    private ViewGroup parent;
    ArrayList<MyPicture> mInput;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    Context context;
    View v2;

    public PictureAdapter(Context context, ArrayList<MyPicture> input) {
        this.context = context;
        this.mInput = input;

    }

    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        this.parent = parent;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_images, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);
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

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Label Adapter:", "clicked on image: pos "+ position + "ID: " + mInput.get(position).getId());
                Intent intent = new Intent(v.getContext(), TextActivity.class);
                intent.putExtra("text", mInput.get(position).getmText()); // put image data in Intent
                intent.putExtra("pos", position);
                intent.putExtra("id", mInput.get(position).getId());
                ((Activity) context).startActivityForResult(intent, 1);
            }

        });


        final Bitmap finalBitmap = bitmap;

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(v2.getParent() != null) {
                    ((ViewGroup)v2.getParent()).removeView(v2); // <- fix
                }

                Dialog settingsDialog = new Dialog(context);

                settingsDialog.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

                settingsDialog.setContentView(v2);

                ImageView im= v2.findViewById(R.id.image_dialog);
                im.getMaxHeight();

                im.setImageBitmap(finalBitmap);
                settingsDialog.show();
                return false;
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
