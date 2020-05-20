package edu.dartmouth.cs.myruns4.adapters;


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

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns4.MapInputActivity;
import edu.dartmouth.cs.myruns4.database.ExerciseEntry;
import edu.dartmouth.cs.myruns4.MainMyRunsActivity;
import edu.dartmouth.cs.myruns4.ManualInputActivity;
import edu.dartmouth.cs.myruns4.R;
import edu.dartmouth.cs.myruns4.fragments.HistoryFragment;
import edu.dartmouth.cs.myruns4.models.Exercise;
import edu.dartmouth.cs.myruns4.models.MyGlobals;

public class HistoryAdapterRecycler extends RecyclerView.Adapter<HistoryAdapterRecycler.ViewHolder> {

    ExerciseEntry mEntry;
    public int dele_pos;
    private ArrayList<Exercise> orderList;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private static final String FROM_HISTORY_TAB = "history_tab";
    public static final String ITEM_TO_DELETE = "item";
    private AsyncTask task;

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
    public void onBindViewHolder(final HistoryAdapterRecycler.ViewHolder holder, final int position) {
        final Exercise order = orderList.get(position);

        MyGlobals globs = new MyGlobals();

        int _input = order.getmInputType();
        String __input = globs.getValue_str(globs.IN, _input);

        int _act = order.getmActivityType();
        String __act = globs.getValue_str(globs.ACT, order.getmActivityType());

        String showActivity = globs.getValue_str(globs.IN, order.getmInputType()) + ": "
                    + globs.getValue_str(globs.ACT, order.getmActivityType()) + "  ";

        String showDate = "" + order.getmDateTime();
        String showDistance;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String units = sharedPreferences.getString("list_preference", "");


        if (units.equals("mi")){
            showDistance = "" + KilometersToMiles(order.getmDistance()) + "  " + "mi" + ", ";
        } else {
            String temp_dist = String.valueOf(order.getmDistance());
            showDistance = "" + temp_dist + "  " + "kms" + ", ";
        }

        String temp_dur = String.valueOf(order.getmDuration());
        String showDuration = "" + temp_dur + " mins";

        holder.hist_activity.setText(showActivity);
        holder.hist_date.setText(showDate);
        holder.hist_distance.setText(showDistance);
        holder.hist_duration.setText(showDuration);

        //Here, calling itemView (equivalent of listAdapter.getView()) and setting a onClickListener as an example. You can do whatever you want.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int input = order.getmInputType();
                if (order.getmInputType() == 0) {
                    Intent intent = new Intent(v.getContext(), ManualInputActivity.class);
                    intent.putExtra(ManualInputActivity.MANUAL_INTENT_FROM, FROM_HISTORY_TAB);
                    intent.putExtra(ManualInputActivity.DELETE_EXERCISE, String.valueOf(order.getId()));
                    intent.putExtra(ManualInputActivity.DELETE_ITEM, String.valueOf(position));
                    ((MainMyRunsActivity) v.getContext()).startActivityForResult(intent, 1);
                    dele_pos = position;
//                } else if(order.getmInputType() == 1){
                } else {
                    Toast.makeText(context.getApplicationContext(),
                            "GPS ATTEMPT TO LOAD DATA",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), MapInputActivity.class);
                    intent.putExtra(MapInputActivity.FROM, FROM_HISTORY_TAB);
                    intent.putExtra(MapInputActivity.DELETE_EXERCISE, String.valueOf(order.getId()));
                    intent.putExtra(MapInputActivity.DELETE_ITEM, String.valueOf(position));
                    ((MainMyRunsActivity) v.getContext()).startActivityForResult(intent,1);
                    dele_pos = position;
                }
            }

        });
    }

    public void onActivityResult(long res) {
        Log.d("Adapter", "onactivity request 1=>delete & 0=> insert" + res);
        if (res == -10) {
            Log.d("Adapter", "onactivity result DELETE: " + dele_pos);
            orderList.remove(dele_pos);
            HistoryFragment.onActivityResult();
        } else {
            Log.d("Adapter", "User did not delete" + res);
                String[] mytasks = {String.valueOf(res)};
            task = new AsyncExerciseLoad().execute(mytasks);
        }
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

    public String KilometersToMiles(double kilo) {
        double miles = kilo * 0.621371;
        String formatted = String.format("%.2f", miles);
        return formatted;
    }

    class AsyncExerciseLoad extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... _id) {
            long id = Long.parseLong(_id[0]);
            Log.d("History Adapter", "Updating history frag UI Exercise ID:  " + id);
            ExerciseEntry mEntry = new ExerciseEntry(context);
            mEntry.open();
            Exercise ee = mEntry.fetchEntryByIndex(id);
            mEntry.close();
            orderList.add(ee);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            task = null;
        }

    }

}
