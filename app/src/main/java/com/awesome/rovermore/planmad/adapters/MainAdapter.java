package com.awesome.rovermore.planmad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesome.rovermore.planmad.R;
import com.awesome.rovermore.planmad.datamodel.Event;
import com.awesome.rovermore.planmad.network.NetworkUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyEventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private onEventClickListener eventClickListener;

    public MainAdapter(@NonNull Context context, List<Event> eventList, onEventClickListener eventClickListener) {
        this.context = context;
        //this.eventList = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull MyEventViewHolder myEventViewHolder, int i){
        Event event = eventList.get(i);
        String textDate = "No date found";
        Date date = event.getDtstart();
        if(date != null) {
            textDate = NetworkUtils.fromDateToString(date);
        }
        myEventViewHolder.tvDate.setText(textDate);
        myEventViewHolder.tvTitle.setText(event.getTitle());
        myEventViewHolder.tvLocation.setText(event.getEventLocation());

        String eventType = event.getEventType();

        if(eventType != null && eventType.equals("ActividadesCalleArteUrbano")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.arte_urbano);
        }
        if(eventType != null && eventType.equals("Campamentos") ) {
            myEventViewHolder.imageView.setImageResource(R.drawable.campamentos);
        }
        if(eventType != null && eventType.equals("CineActividadesAudiovisuales")){
            myEventViewHolder.imageView.setImageResource(R.drawable.cine);
        }
        if (eventType != null && eventType.equals("CircoMagia")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.circo);
        }
        if (eventType != null && eventType.equals("ConcursosCertamenes")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.concurso);
        }
        if (eventType != null && eventType.equals("ConferenciasColoquios")){
            myEventViewHolder.imageView.setImageResource(R.drawable.conference);
        }
        if (eventType != null && eventType.equals("CuentacuentosTite")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.cuenta_cuentos);
        }
        if (eventType != null && eventType.equals("CuentacuentosTiteresMarionetas")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.cuenta_cuentos);
        }
        if (eventType != null && eventType.equals("CursosTalleres")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.cursos_talleres);
        }
        if (eventType != null && eventType.equals("DanzaBaile")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.danza);
        }
        if (eventType != null && eventType.equals("ExcursionesItinerariosVisitas")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.district);
        }
        if (eventType != null && eventType.equals("Exposiciones")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.exposiciones);
        }
        if (eventType != null && eventType.equals("FiestasNavidadesReyes")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.actos_religiosos);
        }
        if (eventType != null && eventType.equals("FiestasSemanaSanta")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.actos_religiosos);
        }
        if (eventType != null && eventType.equals("Musica")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.musica);
        }
        if (eventType != null && eventType.equals("ProgramacionDestacadaAgendaCultura")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.exposiciones);
        }
        if (eventType != null && eventType.equals("RecitalesPresentacionesActosLiterarios")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.teatro);
        }
        if (eventType != null && eventType.equals("TeatroPerformance")) {
            myEventViewHolder.imageView.setImageResource(R.drawable.teatro);
        }
        if (eventType == null) {
            myEventViewHolder.imageView.setImageResource(R.drawable.district);
        }

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
        ImageView imageView;

        public MyEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_main_date);
            tvTitle = itemView.findViewById(R.id.tv_main_title);
            tvLocation = itemView.findViewById(R.id.tv_main_location);
            imageView = itemView.findViewById(R.id.image_view);

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
        //Creating a new ArrayList to be independent from main activity eventList
        if(eventList!=null) {
            this.eventList = new ArrayList<>(eventList);
            notifyDataSetChanged();
        }
    }

    public List<Event> getEventList(){
        return eventList;
    }
}
