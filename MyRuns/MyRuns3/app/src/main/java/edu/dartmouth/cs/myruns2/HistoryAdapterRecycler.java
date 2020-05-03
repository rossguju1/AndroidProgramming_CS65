package edu.dartmouth.cs.myruns2;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        holder.hist_activity.setText(globs.getValue_str("ACTIVITY_TYPES", order.getmActivityType()));
        holder.hist_date.setText(order.getmDateTime());
        holder.hist_distance.setText(String.valueOf(order.getmDistance()));
        holder.hist_duration.setText(String.valueOf(order.getmDuration()));


        Log.d("here1: -> ",  order.getmDateTime() + order.getmComment() + order.getmDuration());
       // holder.orderNumber.setText(String.valueOf(order.getId()));

        //Here, calling itemView (equivalent of listAdapter.getView()) and setting a onClickListener as an example. You can do whatever you want.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, OrderDetailActivity.class);
//                intent.putExtra("order", order);
//                context.startActivity(intent);
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
}
