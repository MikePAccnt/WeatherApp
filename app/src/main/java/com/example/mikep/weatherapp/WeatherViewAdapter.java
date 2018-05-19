package com.example.mikep.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherViewAdapter extends RecyclerView.Adapter<WeatherViewAdapter.WeatherViewHolder> {

    private ArrayList<WeatherDayInfo> data;

    public WeatherViewAdapter(ArrayList<WeatherDayInfo> data){
        this.data = data;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        //TextView cityName = holder.cityName;

        TextView weatherText = holder.weatherText;
        TextView tempText = holder.tempText;
        TextView dateText = holder.dateText;
        TextView dayText = holder.dayText;

        weatherText.setText(data.get(position).getWeatherDescription());
        tempText.setText(String.valueOf((int)data.get(position).getTemperatureF()));
        dateText.setText(data.get(position).getDateAndTime());
        dayText.setText(data.get(position).getDayOfWeek());
    }

    public void replaceDataSet(ArrayList<WeatherDayInfo> info) {
        this.data = info;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        //TextView cityName;
        @BindView(R.id.weatherText2) TextView weatherText;
        @BindView(R.id.tempText2) TextView tempText;
        @BindView(R.id.dateText2) TextView dateText;
        @BindView(R.id.dayText2) TextView dayText;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
