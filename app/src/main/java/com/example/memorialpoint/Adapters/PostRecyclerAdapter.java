package com.example.memorialpoint.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memorialpoint.Fragments.NMapFragment;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.PostComments;
import com.example.memorialpoint.R;
import com.example.memorialpoint.TabMain;
import com.example.memorialpoint.Util.CustomPopUpMenu;

import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ItemViewHolder> {

    Context context;

    public PostRecyclerAdapter(Context context) {
        this.context = context;
    }

    private ArrayList<PostData> pdList = new ArrayList<>();
    String TAG = "Memorial.PostRecyclerAdapter.";

    private onItemClickListener itemClickListener;

    public interface OnLikeClickListener {
        void onLikeClicked(int position);
    }

    OnLikeClickListener onLikeClickListener;

    public void setOnLikeClickListener(@NonNull OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    interface onItemClickListener {
        void itemClick(PostData pd, boolean value);
    }

    public void setItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_post_recycler, viewGroup, false);

   //     context = view.getContext();

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.ItemViewHolder itemViewHolder, int i) {

        itemViewHolder.onBind(pdList.get(i), i);
        // 리사이클러뷰의 순서 i는 순번

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemClickListener != null) {
                    itemClickListener.itemClick(pdList.get(i), true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdList.size();
    }

    @Override
    public long getItemId(int position) {
        return pdList.get(position).getNo();
    }

    public void addItem(PostData data) {
        pdList.add(data);
        Log.d(TAG + "addItem", "아이템 추가");
        notifyDataSetChanged();
    }

    public void updateItem(int i, PostData data) {
        pdList.add(i, data);
        Log.d(TAG + "updateItem", "아이템 추가/갱신");
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Log.d(TAG + "removeItem", "아이템 삭제");
        pdList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems() {
        Log.d(TAG + "removeItem", "아이템들 삭제");
        pdList.clear();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView postHostImg;
        TextView postHostID;
        ImageButton menuIcon;

        TextView cAddressText;
        TextView dAddressText;

        ImageView postImg;
        TextView contentText, addText;

        ImageButton commentBtn, heartBtn;

        Call<ResponseData> likeCall;

        CustomPopUpMenu customPop;
        PopupMenu popupMenu;


        public void init() {
            postHostImg = itemView.findViewById(R.id.globalPostHostImg);
            postHostID = itemView.findViewById(R.id.globalPostHostID);
            menuIcon = itemView.findViewById(R.id.globalMenuIcon);
            cAddressText = itemView.findViewById(R.id.globalCAddressText);
            dAddressText = itemView.findViewById(R.id.globalDAddressText);
            postImg = itemView.findViewById(R.id.globalPostImg);
            contentText = itemView.findViewById(R.id.globalContentText);
            addText = itemView.findViewById(R.id.globalAddText);
            commentBtn = itemView.findViewById(R.id.globalCommentBtn);
            heartBtn = itemView.findViewById(R.id.globalHeartBtn);
        }

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            init();

        }

        public void onBind(PostData data, int i) {

            customPop = new CustomPopUpMenu(context, menuIcon);

            //유저 아이디 or 이메일
            postHostID.setText(data.getWriter());
            postHostID.setSelected(true);

            //Post의 프로필
            try {
                //상대 경로를 URL로 변경
                String path = MyApplication.ip + "memorial_point" + data.getProfile().substring(2);
                URL profileUri = new URL(path);

                MyApplication.ViewCircuitCrop(context, profileUri, postHostImg);

            } catch (Exception e) {
                e.printStackTrace();
                MyApplication.ViewCircuitCrop(context, R.drawable.p_nmap_blank_person, postHostImg);
            }

            //유저 본인인지 타인지에 따라 레이아웃 파일 변경
            if (MyApplication.USER_ID.equals(data.getWriter())) {
                popupMenu = customPop.showPopUp(R.menu.my_post_menu);

                //포스트 우측 상단의 팝업 메뉴
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();

                                switch (id) {
//                                    case R.id.pm_move:
//                                        MyApplication.sendToast(context, item.getTitle().toString());
//                                        return true;
                                    case R.id.pm_remove:
                                        Call<ResponseData> removeCall = MyApplication.conn.retrofitService.removePost(data.getNo());
                                        removeCall.enqueue(new Callback<ResponseData>() {
                                            @Override
                                            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                                pdList.remove(i);
                                                notifyItemRemoved(i);
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseData> call, Throwable t) {
                                                MyApplication.sendToast(context, "삭제 실패");
                                            }
                                        });

                                        MyApplication.sendToast(context, item.getTitle().toString());
                                        return true;
//                                    case R.id.pm_revise:
//                                        MyApplication.sendToast(context, item.getTitle().toString());
//                                        return true;
                                    default:
                                        return true;

                                }
                            }
                        });

                        popupMenu.show();
                    }
                });

            } else {
                menuIcon.setVisibility(View.GONE);
            }



            //Post의 센터 이미지
            try {
                //상대 경로를 URL로 변경
                String path = MyApplication.ip + "memorial_point" + data.getUri().substring(2);

                URL tmpUrl = new URL(path);

                Glide.with(context)
                        .load(tmpUrl)
                        .placeholder(R.drawable.p_empty_image)
                        .error(R.drawable.p_empty_image)
                        .into(postImg);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                // noinspection deprecation
                if (data.getcAddress() != null)
                    cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + data.getcAddress()));
                else
                    cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  ."));


                if (data.getdAddress() != null)
                    dAddressText.setText(Html.fromHtml("<b>" + "상세" + "</b>" + "  " + data.getdAddress()));
                else
                    dAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  ."));


                contentText.setText(Html.fromHtml("<b>" + data.getWriter() + "</b>" + "  " + data.getContents()));
            } else {
                if (data.getcAddress() != null)
                    cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + data.getcAddress(), Html.FROM_HTML_MODE_LEGACY));
                else
                    cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  .", Html.FROM_HTML_MODE_LEGACY));


                if (data.getdAddress() != null)
                    dAddressText.setText(Html.fromHtml("<b>" + "상세" + "</b>" + "  " + data.getdAddress(), Html.FROM_HTML_MODE_LEGACY));
                else
                    dAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  .", Html.FROM_HTML_MODE_LEGACY));

                contentText.setText(Html.fromHtml("<b>" + data.getWriter() + "</b>" + "  " + data.getContents(), Html.FROM_HTML_MODE_LEGACY));
            }

            if (data.getFriend() != null) {
                contentText.append("\n" + data.getFriend());
            }

            if (data.getHashTag() != null) {
                contentText.append("\n" + data.getHashTag());
            }

            if (addText != null) {
                addText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentText.setMaxLines(Integer.MAX_VALUE);
                        addText.setVisibility(View.GONE);
                    }
                });
            }

            final TextView postTextView = contentText;

            postTextView.post(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG + "Context", "줄 사이즈: " + contentText.getLineCount());

                    if (postTextView.getLineCount() > 1) {
                        Log.d(TAG + "onBind", "2줄 이상 - 더보기 생성" + postTextView.getLineCount());
                        postTextView.setMaxLines(1);
                        addText.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG + "onBind", "1줄 - 더보기 감추기" + postTextView.getLineCount());
                        addText.setVisibility(View.GONE);
                    }
                }
            });

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("post", data);
                    context.startActivity(intent);
                }
            });

            //좋아요 여부에 따라 아이콘 변경
            if (data.getLike_no() > 0) {
                heartBtn.setSelected(true);
            } else {
                heartBtn.setSelected(false);
            }

            //좋아요 버튼 이벤트
            heartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (heartBtn.isSelected()) {
                        Log.d(TAG + "Like", "좋아요가 해제됩니다.");
                        likeCall = MyApplication.conn.retrofitService.getLike(0, MyApplication.USER_ID, data.getNo());

                        likeCall.enqueue(new Callback<ResponseData>() {
                            @Override
                            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                Log.d(TAG, "onResponse: " + response.body().getResponseData());

                                pdList.get(i).setLike_no(0);

                                try {
                                    onLikeClickListener.onLikeClicked(i);
                                } catch (Exception e) {
                                }

                                heartBtn.setSelected(false);
                            }

                            @Override
                            public void onFailure(Call<ResponseData> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                Toast.makeText(context, "통신 에러로 인해 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {
                        Log.d(TAG + "Like", "좋아요가 체크됩니다.");

                        likeCall = MyApplication.conn.retrofitService.getLike(1, MyApplication.USER_ID, data.getNo());

                        likeCall.enqueue(new Callback<ResponseData>() {
                            @Override
                            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                Log.d(TAG, "onResponse: " + response.body().getResponseData());
                                heartBtn.setSelected(true);

                                pdList.get(i).setLike_no(1);
                            }

                            @Override
                            public void onFailure(Call<ResponseData> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                Toast.makeText(context, "통신 에러로 인해 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                }
            });
        }
    }
}
