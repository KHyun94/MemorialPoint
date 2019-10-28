package com.example.memorialpoint.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memorialpoint.Models.ReceiveMsg;
import com.example.memorialpoint.R;

import java.util.ArrayList;

public class ChattingClientRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ReceiveMsg> items;

    Context mContext;

    //메시지 타입 => 서버 메시지:0, 타인 메시지:1, 본인 메시지:2
    final int VIEW_TYPE_SERVER = 0;
    final int VIEW_TYPE_OTHER = 1;
    final int VIEW_TYPE_MY = 2;

    public ChattingClientRecyclerAdapter(ArrayList<ReceiveMsg> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {

        View view = null;

        switch (type) {
            case VIEW_TYPE_SERVER:
                LayoutInflater serverLif = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = serverLif.inflate(R.layout.adapter_chatting_server_client_recycler, viewGroup, false);
                ServerViewHolder svh = new ServerViewHolder(view);
                return svh;
            case VIEW_TYPE_OTHER:
                LayoutInflater otherLif = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = otherLif.inflate(R.layout.adapter_chatting_other_client_recycler, viewGroup, false);
                OtherViewHolder ovh = new OtherViewHolder(view);
                return ovh;
            case VIEW_TYPE_MY:
                LayoutInflater myLif = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = myLif.inflate(R.layout.adapter_chatting_my_client_recycler, viewGroup, false);
                MyViewHolder mvh = new MyViewHolder(view);
                return mvh;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        final ReceiveMsg rMsg = items.get(position);

        //해당 메시지 객체의 type을 받아온다.
        int type = rMsg.getType();

        switch (type) {
            case VIEW_TYPE_SERVER:
                final ServerViewHolder svh = (ServerViewHolder) viewHolder;
                svh.server_sendMsg.setText(rMsg.getMsg());
                break;
            case VIEW_TYPE_OTHER:
                final OtherViewHolder ovh = (OtherViewHolder) viewHolder;

                ovh.other_userID.setText(rMsg.getUserID());
                ovh.other_speechText.setText(rMsg.getMsg());
                ovh.other_sendTime.setText(rMsg.getDate());
                break;
            case VIEW_TYPE_MY:
                final MyViewHolder mvh = (MyViewHolder) viewHolder;
                mvh.my_speechText.setText(rMsg.getMsg());
                mvh.my_sendTime.setText(rMsg.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {

        int type = items.get(position).getType();

        switch (type) {
            case VIEW_TYPE_SERVER:
                return VIEW_TYPE_SERVER;
            case VIEW_TYPE_OTHER:
                return VIEW_TYPE_OTHER;
            case VIEW_TYPE_MY:
                return VIEW_TYPE_MY;
        }

        return VIEW_TYPE_SERVER;
    }

    public void addItem(ReceiveMsg receiveMsg) {
        items.add(receiveMsg);
        notifyItemInserted(getItemCount() - 1);
    }

    class ServerViewHolder extends RecyclerView.ViewHolder {

        TextView server_sendMsg;

        public ServerViewHolder(@NonNull View itemView) {
            super(itemView);

            server_sendMsg = itemView.findViewById(R.id.server_sendMsg);
        }
    }

    class OtherViewHolder extends RecyclerView.ViewHolder {

        ImageView other_profileImg;
        TextView other_userID;
        TextView other_speechText;
        TextView other_sendTime;

        public OtherViewHolder(@NonNull View itemView) {
            super(itemView);

            other_profileImg = itemView.findViewById(R.id.other_profileImg);
            other_userID = itemView.findViewById(R.id.other_userID);
            other_speechText = itemView.findViewById(R.id.other_speechText);
            other_sendTime = itemView.findViewById(R.id.other_sendTime);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView my_speechText;
        TextView my_sendTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            my_speechText = itemView.findViewById(R.id.my_speechText);
            my_sendTime = itemView.findViewById(R.id.my_sendTime);
        }
    }


}
