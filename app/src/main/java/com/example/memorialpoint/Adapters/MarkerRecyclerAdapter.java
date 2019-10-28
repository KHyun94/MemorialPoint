package com.example.memorialpoint.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.memorialpoint.Models.Place;
import com.example.memorialpoint.R;

import java.util.ArrayList;

public class MarkerRecyclerAdapter extends RecyclerView.Adapter<MarkerRecyclerAdapter.ItemViewHolder> {

    Context mContext;
    private ArrayList<Place> placeArrayList = new ArrayList<Place>();

    private onItemClickListener itemClickListener;

    public interface onItemClickListener{
        void itemClick(Place place, boolean value);
    }

    public void setItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_search_item, viewGroup, false);

        mContext = view.getContext();

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {

        itemViewHolder.onBind(placeArrayList.get(i));

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(itemClickListener != null)
                {
                    itemClickListener.itemClick(placeArrayList.get(i), true);
                }
                else{
                    Log.d("TAG", "onClick: 없었엉~");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public void addItem(Place data) {
        placeArrayList.add(data);
    }

    // adapter에 들어갈 list 입니다.


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textDistance;
        TextView textAddress;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.mr_textName);
            textAddress = itemView.findViewById(R.id.mr_textAddress);
            textDistance = itemView.findViewById(R.id.mr_textDistance);
        }

        public void onBind(Place data) {

            double d = Double.parseDouble(data.getDistance());

            String tmpStr = String.format("%.0f", d);

            if (tmpStr.length() > 3) {

                d = Double.parseDouble(tmpStr);

                tmpStr = String.format("%.1f", (d * 0.001)) + "Km";

            } else {
                tmpStr += "m";
            }

            textName.setText(data.getName());
            textAddress.setText(data.getJibun_address());
            textDistance.setText(tmpStr);

        }
    }
}
