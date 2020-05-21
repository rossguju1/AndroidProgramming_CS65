package edu.dartmouth.cs.myorganizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private ArrayList<String> orderList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public FilesAdapter(Context context, ArrayList<String> list) {
        this.orderList = list;
        //this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      //  View view = mLayoutInflater.inflate(R.layout.recycler_item_order, parent, false);

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.recycler_item_order, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final FilesAdapter.ViewHolder holder, final int position) {
        String order = orderList.get(position);
        Log.d("FilesAdapter", order);



        holder.imageUri.setText(order);


        //Here, calling itemView (equivalent of listAdapter.getView()) and setting a onClickListener as an example. You can do whatever you want.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(context.getApplicationContext(),
                            "Clicked",
                            Toast.LENGTH_SHORT).show();


            }

        });
    }



    @Override
    public int getItemCount() {
        return orderList.size();
    }

    //In the ViewHolder you declare all the components of your xml layout for you recyclerView items ( and the data is assigned in the onBindViewHolder, above)
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView imageUri;
        private ViewHolder(View itemView) {
            super(itemView);
            imageUri = (TextView) itemView.findViewById(R.id.ui_image_uri);

        }

    }


}
