package edu.dartmouth.cs.myruns2;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns2.models.Exercise;
import edu.dartmouth.cs.myruns2.ExerciseEntryDbHelper;
import edu.dartmouth.cs.myruns2.models.MyGlobals;

public class HistoryAdapterRecycler extends RecyclerView.Adapter<HistoryAdapterRecycler.ViewHolder> {
    ExerciseEntry mEntry;
    private ArrayList<Exercise> orderList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    private static final String FROM_HISTORY_TAB = "history_tab";
    public HistoryAdapterRecycler(Context context, ArrayList<Exercise> list) {


        this.orderList = list;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public HistoryAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.recycler_item_order, parent, false);
        return new HistoryAdapterRecycler.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryAdapterRecycler.ViewHolder holder, int position) {
        final Exercise order = orderList.get(position);




        MyGlobals globs = new MyGlobals();




            int _input = order.getmInputType();
            Log.d("Histo", "INTEGER INPUT type  INT : " + _input);
            String __input = globs.getValue_str(globs.IN, _input);
            Log.d("Histo", "STRINGGGG INPUT type STR : " + __input);

            int _act = order.getmActivityType();
            Log.d("Histo", "INTEGER ACT type  STR _act : " + _act);
            String __act = globs.getValue_str(globs.ACT, order.getmActivityType());
            Log.d("Histo", "STRING ACT type  STR _act : " + __act);


            String showActivity = globs.getValue_str(globs.IN, order.getmInputType()) + ": "
                    + globs.getValue_str(globs.ACT, order.getmActivityType()) + "  ";

            String showDate = "Date: " + order.getmDateTime();
        String showDistance;

        if (globs.CURRENT_UNITS == 1 ){
            showDistance = "Distance: " + KilometersToMiles(order.getmDistance()) + "  " + globs.getValue_str(globs.UNIT_TABLE, globs.CURRENT_UNITS) + " ";


        } else {
            String temp_dist = String.valueOf(order.getmDistance());
            showDistance = "Distance: " + temp_dist + "  " + globs.getValue_str(globs.UNIT_TABLE, globs.CURRENT_UNITS) + " ";
        }


            String temp_dur = String.valueOf(order.getmDuration());

            String showDuration = "Duration: " + temp_dur + " hours";

            holder.hist_activity.setText(showActivity);
            holder.hist_date.setText(showDate);
            holder.hist_distance.setText(showDistance);
            holder.hist_duration.setText(showDuration);



        Log.d("here1: -> ",  order.getmDateTime() + order.getmComment() + order.getmDuration());
       // holder.orderNumber.setText(String.valueOf(order.getId()));

        //Here, calling itemView (equivalent of listAdapter.getView()) and setting a onClickListener as an example. You can do whatever you want.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("HISTORYFRAGMENT", "YOU CLICKED ON ID: " + order.getId());
                Intent intent = new Intent(context, ManualInputActivity.class);
                intent.putExtra(ManualInputActivity.MANUAL_INTENT_FROM, FROM_HISTORY_TAB);
                intent.putExtra(ManualInputActivity.DELETE_EXERCISE, String.valueOf(order.getId()));

                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return orderList.size();
    }

    //In the ViewHolder you declare all the components of your xml layout for you recyclerView items ( and the data is assigned in the onBindViewHolder, above)
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView hist_activity, hist_date, hist_distance, hist_duration;

        private ViewHolder(View itemView) {
            super(itemView);
            hist_activity = (TextView) itemView.findViewById(R.id.hist_activity_type);
            hist_date = (TextView) itemView.findViewById(R.id.hist_date);
            hist_distance = (TextView) itemView.findViewById(R.id.hist_distance);
            hist_duration = (TextView) itemView.findViewById(R.id.hist_duration);

        }
    }

    public String KilometersToMiles(double kilo){

        double miles = kilo * 0.621371;

        String formatted = String.format("%.2f", miles);
    return formatted;
    }
}
