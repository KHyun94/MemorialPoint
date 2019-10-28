package com.example.memorialpoint.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memorialpoint.Models.Comments;
import com.example.memorialpoint.Models.GroupComments;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;

import java.net.URL;
import java.util.ArrayList;

public class PostCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int GROUP = 0, CHILD = 1;

    String TAG = "TAG";
    private ArrayList<GroupComments> data;
    Context context;

    //------------------------------------------
    public interface onReplyClickListener {
        void onReplyClick(int no, int position);
    }

    private onReplyClickListener replyClickListener;

    //------------------------------------------

    private onItemClickListener itemClickListener;

    public interface onItemClickListener {
        void itemClick(int position, int commentsNo);
    }

    public void setItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    //-------------------------------------------

    public PostCommentsAdapter(ArrayList<GroupComments> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {

        View view = null;

        switch (type) {
            case GROUP:
                LayoutInflater groupLif = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = groupLif.inflate(R.layout.expandable_comments_group_row, parent, false);
                ListGroupViewHolder group = new ListGroupViewHolder(view);
                return group;
            case CHILD:
                LayoutInflater childLif = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = childLif.inflate(R.layout.expandable_comments_child_row, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child;
        }

        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final GroupComments item = data.get(position);

        int type;

        if (item.getComments().getGroupNo() == 0) {
            type = 0;
        } else {
            type = 1;
        }

        switch (type) {
            case GROUP:
                final ListGroupViewHolder group = (ListGroupViewHolder) holder;

                group.comments_parents_profile.setFocusable(false);

                replyClickListener = (onReplyClickListener) context;

                //group view가 child view를 가지고 있을 떄의 개수
                if (item.getComments().cnt != 0) {
                    group.comments_parents_reply.setText("답글 달기(" + item.getComments().cnt + ")");
                }

                //댓글 작성자의 프로필 이미지를 삽입
                try {
                    //상대 경로를 URL로 변경
                    String path;

                    path = MyApplication.ip + "memorial_point" + item.getComments().getUrl().substring(2);

                    Log.d(TAG, "댓글 프로필 주소: " + path);

                    URL profileUri = new URL(path);

                    MyApplication.ViewCircuitCrop(context, profileUri, group.comments_parents_profile);

                } catch (Exception e) {
                    e.printStackTrace();
                    MyApplication.ViewCircuitCrop(context, R.drawable.p_nmap_blank_person, group.comments_parents_profile);
                }

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                    group.comments_parents_comments.setText(Html.fromHtml("<b>" + item.getComments().getId() + "</b>" + "  " + item.getComments().getComments()));
                } else {
                    group.comments_parents_comments.setText(Html.fromHtml("<b>" + item.getComments().getId() + "</b>" + "  " + item.getComments().getComments(), Html.FROM_HTML_MODE_LEGACY));
                }

                group.comments_parents_date.setText(item.getComments().getDate());

                group.comments_parents_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int commentsNo = item.getComments().getNo();
                        replyClickListener.onReplyClick(position, commentsNo);
                    }
                });

                group.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int commentsNo = item.getComments().getNo();
                        itemClickListener.itemClick(position, commentsNo);
                    }
                });

                break;
            case CHILD:
                final ListChildViewHolder child = (ListChildViewHolder) holder;

                child.comments_child_profile.setFocusable(false);

                try {
                    //상대 경로를 URL로 변경

                    String path = MyApplication.ip + "memorial_point" + item.getComments().getUrl().substring(2);

                    URL profileUri = new URL(path);

                    MyApplication.ViewCircuitCrop(context, profileUri, child.comments_child_profile);

                } catch (Exception e) {
                    e.printStackTrace();
                    MyApplication.ViewCircuitCrop(context, R.drawable.p_nmap_blank_person, child.comments_child_profile);
                }

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                    // noinspection deprecation
                    child.comments_child_comments.setText(Html.fromHtml("<b>" + item.getComments().getId() + "</b>" + "  " + item.getComments().getComments()));
                } else {
                    child.comments_child_comments.setText(Html.fromHtml("<b>" + item.getComments().getId() + "</b>" + "  " + item.getComments().getComments(), Html.FROM_HTML_MODE_LEGACY));
                }

                child.comments_child_date.setText(item.getComments().getDate());

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        Log.d(TAG, position + "번째: " + "커맨트 번호: " + data.get(position).getComments().getNo()
                + ", 부모 번호: " + data.get(position).getComments().getGroupNo());
        if (data.get(position).getComments().getGroupNo() == 0)
            return GROUP;
        else
            return CHILD;
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getComments().getNo();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(Comments c, int position) {

        Log.d(TAG, "addItem");

        if (c.getGroupNo() == 0) {
            // 부모 뷰를 올릴 때
            data.add(new GroupComments(c));
        } else {
            // 자식 뷰를 올릴 때

            int SIZE = data.get(position).childList.size();
            data.get(position).childList.add(c);
            data.add((position + SIZE + 1), new GroupComments(c));
        }

        notifyDataSetChanged();
    }

    public void updateItem() {

    }

    public void removeItem() {

    }

    public void expandListener(int position, Comments c) {

        data.get(position).childList.add(c);

        int SIZE = data.get(position).childList.size();
        data.add(position + SIZE, new GroupComments(c));

        notifyDataSetChanged();

    }

    public void collapseListener(int position) {

        int SIZE = data.get(position).childList.size();

        for (int i = SIZE + position; i > position; i--) {
            data.remove(i);
        }

        data.get(position).childList = new ArrayList<>();
        notifyDataSetChanged();
    }

    private class ListGroupViewHolder extends RecyclerView.ViewHolder {
        ImageView comments_parents_profile;
        TextView comments_parents_comments;
        TextView comments_parents_reply;
        TextView comments_parents_date;

        public ListGroupViewHolder(View itemView) {
            super(itemView);

            comments_parents_profile = itemView.findViewById(R.id.comments_parents_profile);
            comments_parents_comments = itemView.findViewById(R.id.comments_parents_comments);
            comments_parents_reply = itemView.findViewById(R.id.comments_parents_reply);
            comments_parents_date = itemView.findViewById(R.id.comments_parents_date);
        }
    }

    private class ListChildViewHolder extends RecyclerView.ViewHolder {
        ImageView comments_child_profile;
        TextView comments_child_comments;
        TextView comments_child_date;

        public ListChildViewHolder(View itemView) {
            super(itemView);

            comments_child_profile = itemView.findViewById(R.id.comments_child_profile);
            comments_child_comments = itemView.findViewById(R.id.comments_child_comments);
            comments_child_date = itemView.findViewById(R.id.comments_child_date);
        }
    }
}
