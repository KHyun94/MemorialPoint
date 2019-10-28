package com.example.memorialpoint.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memorialpoint.Models.Place;
import com.example.memorialpoint.R;

import java.util.ArrayList;
import java.util.List;

public class MapSearchRecyclerAdapter extends RecyclerView.Adapter<MapSearchRecyclerAdapter.ItemViewHolder> {

    Context mContext;
    private List<Place> placeList = new ArrayList<Place>();
    boolean isUserClear;

    private OnPlaceClickListener placeClickListener;
    private OnClearClickListener clearClickListener;

    public interface OnPlaceClickListener {
        void itemClick(Place place);
    }

    public interface OnClearClickListener {
        void clearClick(int position);
    }

    public void setPlaceClickListener(OnPlaceClickListener placeClickListener) {
        this.placeClickListener = placeClickListener;
    }

    public void setClearClickListener(OnClearClickListener clearClickListener) {
        this.clearClickListener = clearClickListener;
    }

    public MapSearchRecyclerAdapter(boolean isUserClear) {
        this.isUserClear = isUserClear;
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


        itemViewHolder.onBind(placeList.get(i));

        if (isUserClear) {
            itemViewHolder.clearBtn.setVisibility(View.VISIBLE);

            itemViewHolder.clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearClickListener.clearClick(i);

                    placeList.remove(i);
                    notifyDataSetChanged();
                }
            });

        } else {
            itemViewHolder.clearBtn.setVisibility(View.GONE);
        }

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placeClickListener != null && placeList.size() > 0) {
                    placeClickListener.itemClick(placeList.get(i));
                } else {
                    Log.d("TAG", "onClick: 없었엉~");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public void addItems(List<Place> list) {

        if (placeList != null) {

            placeList.clear();
            placeList.addAll(list);

        }

        notifyDataSetChanged();
    }

    public void removeItems() {
        placeList.clear();
        notifyDataSetChanged();
    }
    // adapter에 들어갈 list 입니다.


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textDistance;
        TextView textAddress;
        ImageView clearBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.mr_textName);
            textAddress = itemView.findViewById(R.id.mr_textAddress);
            textDistance = itemView.findViewById(R.id.mr_textDistance);
            clearBtn = itemView.findViewById(R.id.mr_clearBtn);
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
