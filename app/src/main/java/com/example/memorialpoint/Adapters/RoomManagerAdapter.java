package com.example.memorialpoint.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memorialpoint.Models.RoomData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;

import java.net.URL;
import java.util.ArrayList;

public class RoomManagerAdapter extends RecyclerView.Adapter<RoomManagerAdapter.ItemViewHolder> {

    //대기 목록의 방을 클릭하는 이벤트
    public interface onRoomClickListener {
        void onRoomClicked(RoomData roomData);
    }

    private onRoomClickListener roomClickListener;

    public void setRoomClickListener(onRoomClickListener roomClickListener) {
        this.roomClickListener = roomClickListener;
    }

    Context context;
    ArrayList<RoomData> roomList;

    public RoomManagerAdapter(Context context, ArrayList<RoomData> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_room_manager, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {

        RoomData roomData = roomList.get(position);

        //댓글 작성자의 프로필 이미지를 삽입
        if (roomData.getR_host_img() == null)
            MyApplication.ViewCircuitCrop(context, R.drawable.p_nmap_blank_person, itemViewHolder.rm_profile_img);
        else {
            try {
                //상대 경로를 URL로 변경
                String path;

                path = MyApplication.ip + "memorial_point" + roomData.getR_host_img().substring(2);

                URL profileUri = new URL(path);

                if (profileUri != null)
                    MyApplication.ViewCircuitCrop(context, profileUri, itemViewHolder.rm_profile_img);
                else
                    MyApplication.ViewCircuitCrop(context, R.drawable.p_nmap_blank_person, itemViewHolder.rm_profile_img);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        itemViewHolder.rm_room_name.setText(roomData.getR_no() + "-" + roomData.getR_name());
        itemViewHolder.rm_host_name.setText(roomData.getR_host());
        itemViewHolder.rm_create_date.setText(roomData.getR_date());


        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomClickListener.onRoomClicked(roomList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void addItem(RoomData roomData) {
        roomList.add(roomData);
        notifyDataSetChanged();
    }

    public void removeItem() {
        roomList.clear();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView rm_profile_img;
        TextView rm_room_name;
        TextView rm_host_name;
        TextView rm_create_date;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            rm_profile_img = itemView.findViewById(R.id.rm_profile_img);
            rm_room_name = itemView.findViewById(R.id.rm_room_name);
            rm_host_name = itemView.findViewById(R.id.rm_host_name);
            rm_create_date = itemView.findViewById(R.id.rm_create_date);
        }
    }

}
