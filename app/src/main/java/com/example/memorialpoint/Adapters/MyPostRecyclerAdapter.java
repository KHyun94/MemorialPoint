package com.example.memorialpoint.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;

import java.net.URL;
import java.util.ArrayList;

public class MyPostRecyclerAdapter extends RecyclerView.Adapter<MyPostRecyclerAdapter.ItemViewHolder> {

    public OnPostClickListener onPostClickListener;

    public interface OnPostClickListener{
        void onPostClicked(PostData postData, int position);
    }

    public void setOnPostClickListener(OnPostClickListener listener){
        this.onPostClickListener = listener;

    }

    String TAG= "Memorial.MyPostRecyclerAdapter.";
    ArrayList<PostData> items;
    Context context;

    public MyPostRecyclerAdapter(ArrayList<PostData> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_my_post, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {

        final PostData postData = items.get(position);

        //댓글 작성자의 프로필 이미지를 삽입

        try {
            //상대 경로를 URL로 변경
            String path;

            path = MyApplication.ip + "memorial_point" + postData.getUri().substring(2);

            URL profileUri = new URL(path);

            Glide.with(context)
                    .load(profileUri)
                    .placeholder(R.drawable.p_empty_image)
                    .error(R.drawable.p_empty_image)
                    .into(itemViewHolder.fmp_post_img);

        } catch (Exception e) {
            e.printStackTrace();

            Glide.with(context)
                    .load(R.drawable.p_empty_image)
                    .placeholder(R.drawable.p_empty_image)
                    .error(R.drawable.p_empty_image)
                    .into(itemViewHolder.fmp_post_img);
        }

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                if(onPostClickListener != null){
                    onPostClickListener.onPostClicked(postData, position);
                    Log.d(TAG, "onClick" + postData.getWriter());
                }
                else
                    Log.d(TAG, "onClick:  실패");
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView fmp_post_img;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            fmp_post_img = itemView.findViewById(R.id.fmp_post_img);
        }
    }
}
