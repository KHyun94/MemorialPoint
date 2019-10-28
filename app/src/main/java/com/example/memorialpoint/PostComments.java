package com.example.memorialpoint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.memorialpoint.Adapters.PostCommentsAdapter;
import com.example.memorialpoint.Models.Comments;
import com.example.memorialpoint.Models.GroupComments;
import com.example.memorialpoint.Models.PostData;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostComments extends AppCompatActivity implements Serializable, PostCommentsAdapter.onReplyClickListener {

    String TAG = "Memorial.PostComments.";
    Context mContext;
    RecyclerView expandableLv_comments;
    PostCommentsAdapter adapter;

    ImageView commentsPostProfile;
    TextView post_comments_context;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout replyLayout;
    TextView toReplyTv;
    ImageButton clearText;

    ImageView commentsProfile;
    EditText commentsEt;
    TextView commentsPosting;

    PostData postData;

    ArrayList<GroupComments> commentsList;

    int oldPosition = -1, curPosition = -1;

    int replyNo = 0;
    int postLocation;

    boolean isReply = false;

    public void init() {

        mContext = this;

        commentsPostProfile = (ImageView) findViewById(R.id.post_comments_post_profile);
        post_comments_context = (TextView) findViewById(R.id.post_comments_context);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.comments_swipeRefreshLayout);

        replyLayout = (RelativeLayout) findViewById(R.id.post_comments_to_replyLayout);
        toReplyTv = (TextView) findViewById(R.id.post_comments_to_reply);
        clearText = (ImageButton) findViewById(R.id.post_comments_clear_text);

        commentsProfile = (ImageView) findViewById(R.id.post_comments_profile);
        commentsEt = (EditText) findViewById(R.id.post_comments_et);
        commentsPosting = (TextView) findViewById(R.id.post_comments_posting);

        expandableLv_comments = (RecyclerView) findViewById(R.id.expandableLv_comments);
        //-----------------------------------------------------------------------------------------
        commentsList = new ArrayList<GroupComments>();
    }

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        if (getIntent() != null) {
            postData = (PostData) getIntent().getSerializableExtra("post");
        } else
            finish();

        init();

        //SwipeRefreshLayout의 스크롤 뷰를 하이딩
        mSwipeRefreshLayout.setScrollbarFadingEnabled(true);

        // comments.class의 포스트 호스트의 프로필
        try {
            //상대 경로를 URL로 변경
            String path = MyApplication.ip + "memorial_point" + postData.getProfile().substring(2);

            URL tmpUrl = new URL(path);

            if (tmpUrl != null) {
                MyApplication.ViewCircuitCrop(mContext, tmpUrl, commentsPostProfile);
            } else {
                MyApplication.ViewCircuitCrop(mContext, R.drawable.p_nmap_blank_person, commentsPostProfile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.ViewCircuitCrop(mContext, R.drawable.p_nmap_blank_person, commentsPostProfile);
        }



        // comments.class의 포스트 내용
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            post_comments_context.setText(Html.fromHtml("<b>" + postData.getWriter() + "</b>" + "  " + postData.getContents()));
        } else {
            post_comments_context.setText(Html.fromHtml("<b>" + postData.getWriter() + "</b>" + "  " + postData.getContents(), Html.FROM_HTML_MODE_LEGACY));
        }

        //초기 댓글을 호출
        Call<List<Comments>> groupCall = MyApplication.conn.retrofitService.loadGroupComments(postData.getNo());

        groupCall.enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(Call<List<Comments>> groupCall, Response<List<Comments>> response) {
                List<Comments> list = response.body();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGroupNo() == 0) {
                        commentsList.add(new GroupComments(list.get(i)));
                    }
                }
                expandableLv_comments.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                expandableLv_comments.addItemDecoration(new RecyclerDecoration(20));
                adapter = new PostCommentsAdapter(commentsList, mContext);
                adapter.setHasStableIds(true);
                expandableLv_comments.setAdapter(adapter);

                adapter.setItemClickListener(new PostCommentsAdapter.onItemClickListener() {
                    @Override
                    public void itemClick(int position, int no) {

                        Log.d(TAG, "선택 POSITION: " + position);

                        Call<List<Comments>> childCall = MyApplication.conn.retrofitService.loadChildComments(postData.getNo(), no);

                        if (oldPosition == -1) {
                            // 아무 것도 안펼쳐져있을 때 누를 경우 선택 포지션의 인덱스의 자식 값이 있을 경우
                            // 자식값을 해당 포지션 아래로 표시한다.
                            childCall.enqueue(new Callback<List<Comments>>() {
                                @Override
                                public void onResponse(Call<List<Comments>> childCall, Response<List<Comments>> response) {

                                    List<Comments> list = response.body();
                                    curPosition = position;
                                    //curPosition: 현재 선택한 position

                                    if (list.size() > 0) {
                                        //해당 포지션 + 자식
                                        expandableLv_comments.scrollToPosition(position + list.size());
                                        Log.d(TAG, "이동 스크롤 포지션: " + (position + list.size()));

                                        for (int i = 0; i < list.size(); i++) {
                                            adapter.expandListener(curPosition, list.get(i));
                                            adapter.notifyDataSetChanged();
                                        }

                                        for (int i = 0; i < commentsList.size(); i++) {
                                            Log.d(TAG, "commentsList: " + commentsList.get(i).getComments().getNo());
                                        }
                                        Log.d(TAG, "선택 값의 자식 수: " + commentsList.get(position).childList.size());
                                    }

                                    oldPosition = curPosition;
                                    //oldPosition: 열려 있는 position
                                }

                                @Override
                                public void onFailure(Call<List<Comments>> childCall, Throwable t) {
                                    Log.d(TAG, "onFailure: 차일드 대댓글 네트워크 오류");
                                    t.printStackTrace();
                                }
                            });

                        } else {

                            // 아이템 뷰 중에 자식 뷰가 열려 있을 때
                            // oldPosition: 열려 있는 뷰의 부모 Position

                            int SIZE = commentsList.get(oldPosition).childList.size();
                            //SIZE = 현재 열려있는 부모의 자식 뷰 갯수

                            Log.d(TAG, "열려 있는 자식 뷰의 갯수(SIZE): " + SIZE);


                            if (position == oldPosition) {
                                // 현재 선택값이 열려있는 뷰일 때; 선택 부모 뷰의 인텍스 == 열려있는 부모 뷰의 인덱스

                                // 열려 있는 뷰를 닫는다.
                                adapter.collapseListener(oldPosition);
                                adapter.notifyDataSetChanged();

                                // 닫힐 떄 부모 뷰의 인덱스로 옮겨간다.
                                expandableLv_comments.scrollToPosition(oldPosition);
                                Log.d(TAG, "움직임: " + position);

                                //열려 있는 뷰가 없으므로 초기화한다.
                                oldPosition = -1;

                            } else if (position < oldPosition) {

                                // 선택 부모 뷰의 인덱스 < 열려있는 뷰의 부모 뷰의 인덱스
                                childCall.enqueue(new Callback<List<Comments>>() {
                                    @Override
                                    public void onResponse(Call<List<Comments>> childCall, Response<List<Comments>> response) {

                                        List<Comments> list = response.body();

                                        curPosition = position;

                                        //curPosition: 현재 선택한 position
                                        for (int i = 0; i < list.size(); i++) {
                                            adapter.expandListener(curPosition, list.get(i));
                                            adapter.notifyDataSetChanged();
                                        }
                                        for (int i = 0; i < commentsList.size(); i++) {
                                            Log.d(TAG, "commentsList: " + commentsList.get(i).getComments().getNo());
                                        }
                                        Log.d(TAG, "선택 값의 자식 수: " + commentsList.get(position).childList.size());

                                        oldPosition = curPosition;
                                        //oldPosition: 열려 있는 position

                                        expandableLv_comments.scrollToPosition(position + list.size());
                                        Log.d(TAG, "움직임: " + (position + list.size()));

                                    }

                                    @Override
                                    public void onFailure(Call<List<Comments>> childCall, Throwable t) {
                                        Log.d(TAG, "onFailure: 차일드 대댓글 네트워크 오류");
                                        t.printStackTrace();
                                    }
                                });

                                adapter.collapseListener(oldPosition);
                                adapter.notifyDataSetChanged();
                            } else {

                                curPosition = position - SIZE;
                                //curPosition = 열려있는 부모 뷰의 index보다 더 큰 index 일 때

                                childCall.enqueue(new Callback<List<Comments>>() {
                                    @Override
                                    public void onResponse(Call<List<Comments>> childCall, Response<List<Comments>> response) {

                                        Log.d(TAG, "현재 POSITION(curPosition): " + curPosition);

                                        List<Comments> list = response.body();

                                        //curPosition: 현재 선택한 position

                                        for (int i = 0; i < list.size(); i++) {
                                            adapter.expandListener(curPosition, list.get(i));
                                            adapter.notifyDataSetChanged();
                                        }
                                        for (int i = 0; i < commentsList.size(); i++) {
                                            Log.d(TAG, "commentsList: " + commentsList.get(i).getComments().getNo());
                                        }
                                        expandableLv_comments.scrollToPosition(position + list.size());
                                        Log.d(TAG, "움직임: " + (curPosition + list.size()));

                                        oldPosition = curPosition;
                                        //oldPosition: 열려 있는 position(curPosition)을 삽입
                                    }

                                    @Override
                                    public void onFailure(Call<List<Comments>> childCall, Throwable t) {
                                        Log.d(TAG, "onFailure: 차일드 대댓글 네트워크 오류");
                                        t.printStackTrace();
                                    }
                                });

                                adapter.collapseListener(oldPosition);
                                adapter.notifyDataSetChanged();

                                oldPosition = curPosition;
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Comments>> groupCall, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: 댓글 불러올 때 에러");
            }
        });

        // POST TextView의 변화
        commentsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Thread tvThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (commentsEt.getText().toString().isEmpty() || commentsEt.getText().equals("")) {
                                    isReply = false;
                                    commentsPosting.setTextColor(Color.parseColor("#C5CAE9"));
                                } else {
                                    isReply = true;
                                    commentsPosting.setTextColor(Color.parseColor("#3F51B5"));
                                }
                            }
                        });
                    }
                });
                tvThread.start();
            }
        });

        //replyLayout의 x 아이콘을 누를 때
        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideReplyLayout(mContext);
                MyApplication.hideToKeyBoard(mContext, commentsEt);

                replyNo = 0;
            }
        });


        //댓글 포스팅
        commentsPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isReply) {
                    Log.d(TAG, "포스팅 실행 여부: " + isReply);
                    return;
                } else {
                    Log.d(TAG, "포스팅 실행 여부: " + isReply);

                    Log.d(TAG, "postLocation: " + postLocation);
                    int postNo = postData.getNo();
                    String id = MyApplication.USER_ID;
                    String comments = commentsEt.getText().toString();

                    Call<Comments> call
                            = MyApplication.conn.retrofitService.uploadComments(postNo, replyNo, id, comments);

                    call.enqueue(new Callback<Comments>() {
                        @Override
                        public void onResponse(Call<Comments> call, Response<Comments> response) {

                            Comments c = response.body();

                            adapter.addItem(c, postLocation);


                            commentsEt.setText("");
                            MyApplication.hideToKeyBoard(mContext, commentsEt);

                            hideReplyLayout(mContext);

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Comments> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });


    }

    //reply TextView를 누르면 해당 레이아웃을 띄우고 해당 아이템의 코멘트 번호를 넘긴다.
    public void showReplyLayout(int index, Context context) {

        if (replyLayout.getVisibility() != View.VISIBLE) {
            replyLayout.setVisibility(View.VISIBLE);
            MyApplication.showToKeyBoard(context, commentsEt);
        }

        toReplyTv.setText("Reply to " + commentsList.get(index).getComments().getId());
    }

    public void hideReplyLayout(Context context) {
        replyLayout.setVisibility(View.GONE);

        MyApplication.hideToKeyBoard(context, commentsEt);
        toReplyTv.setText("");

        replyNo = 0;
    }


    @Override
    public void onReplyClick(int position, int commentsNo) {

        // Reply TextView를 클릭 할 시 replyLayout을 보이게 한다.

        Log.d(TAG, "선택 POSITION: " + position);

        replyNo = commentsNo;

        Call<List<Comments>> childCall = MyApplication.conn.retrofitService.loadChildComments(postData.getNo(), commentsNo);

        if (oldPosition == -1) {
            // 아무 것도 안펼쳐져있을 때 누를 경우 선택 포지션의 인덱스의 자식 값이 있을 경우
            // 자식값을 해당 포지션 아래로 표시한다.
            childCall.enqueue(new Callback<List<Comments>>() {
                @Override
                public void onResponse(Call<List<Comments>> childCall, Response<List<Comments>> response) {

                    List<Comments> list = response.body();
                    curPosition = position;
                    //curPosition: 현재 선택한 position
                    postLocation = curPosition;
                    //해당 포지션 + 자식
                    expandableLv_comments.scrollToPosition(position + list.size());
                    Log.d(TAG, "이동 스크롤 포지션: " + (position + list.size()));

                    for (int i = 0; i < list.size(); i++) {
                        adapter.expandListener(curPosition, list.get(i));
                        adapter.notifyDataSetChanged();
                    }

                    showReplyLayout(curPosition, mContext);

                    oldPosition = curPosition;
                    //oldPosition: 열려 있는 position
                }

                @Override
                public void onFailure(Call<List<Comments>> childCall, Throwable t) {
                    Log.d(TAG, "onFailure: 차일드 대댓글 네트워크 오류");
                    t.printStackTrace();
                }
            });

        } else {

            // 아이템 뷰 중에 자식 뷰가 열려 있을 때
            // oldPosition: 열려 있는 뷰의 부모 Position

            int SIZE = commentsList.get(oldPosition).childList.size();
            //SIZE = 현재 열려있는 부모의 자식 뷰 갯수

            Log.d(TAG, "열려 있는 자식 뷰의 갯수(SIZE): " + SIZE);


            if (position == oldPosition) {
                // 현재 선택값이 열려있는 뷰일 때; 선택 부모 뷰의 인텍스 == 열려있는 부모 뷰의 인덱스

                // 열려 있는 뷰를 닫는다.
                adapter.collapseListener(oldPosition);
                adapter.notifyDataSetChanged();

                replyNo = 0;

                // 닫힐 떄 부모 뷰의 인덱스로 옮겨간다.
                expandableLv_comments.scrollToPosition(oldPosition);
                Log.d(TAG, "움직임: " + position);

                hideReplyLayout(mContext);
                //열려 있는 뷰가 없으므로 초기화한다.
                oldPosition = -1;

            } else if (position < oldPosition) {

                // 선택 부모 뷰의 인덱스 < 열려있는 뷰의 부모 뷰의 인덱스
                childCall.enqueue(new Callback<List<Comments>>() {
                    @Override
                    public void onResponse(Call<List<Comments>> childCall, Response<List<Comments>> response) {

                        List<Comments> list = response.body();

                        curPosition = position;
                        postLocation = curPosition;
                        //curPosition: 현재 선택한 position
                        for (int i = 0; i < list.size(); i++) {
                            adapter.expandListener(curPosition, list.get(i));
                            adapter.notifyDataSetChanged();
                        }

                        showReplyLayout(curPosition, mContext);

                        oldPosition = curPosition;
                        //oldPosition: 열려 있는 position

                        expandableLv_comments.scrollToPosition(position + list.size());
                        Log.d(TAG, "움직임: " + (position + list.size()));
                    }

                    @Override
                    public void onFailure(Call<List<Comments>> childCall, Throwable t) {
                        Log.d(TAG, "onFailure: 차일드 대댓글 네트워크 오류");
                        t.printStackTrace();
                    }
                });

                adapter.collapseListener(oldPosition);
                adapter.notifyDataSetChanged();
            } else {

                //curPosition = 열려있는 부모 뷰의 index보다 더 큰 index 일 때

                childCall.enqueue(new Callback<List<Comments>>() {
                    @Override
                    public void onResponse(Call<List<Comments>> childCall, Response<List<Comments>> response) {

                        Log.d(TAG, "현재 POSITION(curPosition): " + curPosition);

                        List<Comments> list = response.body();

                        curPosition = position - SIZE;
                        //curPosition: 현재 선택한 position
                        postLocation = curPosition;
                        for (int i = 0; i < list.size(); i++) {
                            adapter.expandListener(curPosition, list.get(i));
                            adapter.notifyDataSetChanged();
                        }

                        showReplyLayout(curPosition, mContext);

                        expandableLv_comments.scrollToPosition(position + list.size());
                        Log.d(TAG, "움직임: " + (curPosition + list.size()));

                        oldPosition = curPosition;
                        //oldPosition: 열려 있는 position(curPosition)을 삽입
                    }

                    @Override
                    public void onFailure(Call<List<Comments>> childCall, Throwable t) {
                        Log.d(TAG, "onFailure: 차일드 대댓글 네트워크 오류");
                        t.printStackTrace();
                    }
                });

                adapter.collapseListener(oldPosition);
                adapter.notifyDataSetChanged();

                oldPosition = curPosition;
            }
        }

        //-------------------------------------------------

    }

}

class RecyclerDecoration extends RecyclerView.ItemDecoration {

    // RecyclerView Item의 각 간격 클래스
    private final int divHeight;

    public RecyclerDecoration(int divHeight) {
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.top = divHeight;
            outRect.bottom = divHeight;
        }
    }
}