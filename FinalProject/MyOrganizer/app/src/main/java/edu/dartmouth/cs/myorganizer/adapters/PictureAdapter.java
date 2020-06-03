package edu.dartmouth.cs.myorganizer.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;

import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.dartmouth.cs.myorganizer.R;
import edu.dartmouth.cs.myorganizer.TextActivity;
import edu.dartmouth.cs.myorganizer.database.MyPicture;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    //    private ViewGroup parent;
    ArrayList<MyPicture> mInput;
    Context context;
    View v2;

    public PictureAdapter(Context context, ArrayList<MyPicture> input) {
        this.context = context;
        this.mInput = input;

    }

    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_images, parent, false);

        // set the view's size, margins, padding and layout parameters
        v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PictureAdapter.ViewHolder holder, final int position) {

        // set the data in items
        MyPicture res = mInput.get(holder.getAdapterPosition());
        Uri resUri = Uri.parse(res.getmImage());

        Bitmap bitmap = null;
        Bitmap convertedImage = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resUri);
            convertedImage = getResizedBitmap(bitmap, 500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            convertedImage.compress(Bitmap.CompressFormat.JPEG, 40, baos);

        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.image.setImageBitmap(convertedImage);
        holder.date.setText(res.getmDate());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (v2.getParent() != null) {
                    ((ViewGroup) v2.getParent()).removeView(v2); // <- fix
                }

                Dialog settingsDialog = new Dialog(context);

                settingsDialog.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

                settingsDialog.setContentView(v2);

                ImageView im = v2.findViewById(R.id.image_dialog);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}
