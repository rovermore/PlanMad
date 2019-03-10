package com.example.rovermore.planmad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rovermore.planmad.R;
import com.example.rovermore.planmad.datamodel.Event;
import com.example.rovermore.planmad.network.NetworkUtils;

import java.util.Date;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyEventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private onEventClickListener eventClickListener;

    public MainAdapter(@NonNull Context context, List<Event> eventList, onEventClickListener eventClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.eventClickListener = eventClickListener;
    }

    public interface onEventClickListener{
        void onEventClicked(Event event);
    }

    @NonNull
    @Override
    public MyEventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item,viewGroup,false);
        return new MyEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventViewHolder myEventViewHolder, int i) {
        Event event = eventList.get(i);


        String textDate = "No date found";
        Date date = event.getDtstart();
        if(date != null) {
            textDate = NetworkUtils.fromDateToString(date);
        }
        myEventViewHolder.tvDate.setText(textDate);
        myEventViewHolder.tvTitle.setText(event.getTitle());
        myEventViewHolder.tvLocation.setText(event.getEventLocation());
        myEventViewHolder.tvPrice.setText(String.valueOf(event.getPrice()));

    }

    @Override
    public int getItemCount() {
        if(eventList!=null && eventList.size()>0) {
            return eventList.size();
        }else{
            return 0;
        }
    }

    public class MyEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvDate;
        TextView tvTitle;
        TextView tvLocation;
        TextView tvPrice;

        public MyEventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_main_date);
            tvTitle = itemView.findViewById(R.id.tv_main_title);
            tvLocation = itemView.findViewById(R.id.tv_main_location);
            tvPrice = itemView.findViewById(R.id.tv_main_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Event event = eventList.get(position);
            eventClickListener.onEventClicked(event);
        }
    }

    public void clearEventListAdapter(){
        if(eventList!=null) {
            eventList.clear();
            eventList = null;
            notifyDataSetChanged();
        }
    }

    public void setEventList(List<Event> eventList){
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    public List<Event> getEventList(){
        return eventList;
    }
}
