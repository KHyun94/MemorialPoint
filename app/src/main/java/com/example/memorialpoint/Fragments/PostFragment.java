package com.example.memorialpoint.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.memorialpoint.Adapters.PostRecyclerAdapter;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.R;
import com.example.memorialpoint.TabMain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends android.support.v4.app.Fragment  {

    String TAG = "Memorial.PostFragment.";

    RecyclerView postRecycler;
    PostRecyclerAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int cnt = 5;

    String lastDate;

    //latestDay: 끌어당기기를 했을 때 새로운 아이템을 업데이트하기 위한 날짜
    //lastDay: 아래로 내릴 때
    public PostFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof TabMain) {
            mSwipeRefreshLayout = ((TabMain) getActivity()).onSwipeRefresh();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_fragment, container, false);

        postRecycler = (RecyclerView) view.findViewById(R.id.postRecyclerView);

        postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PostRecyclerAdapter(getActivity());
        adapter.setHasStableIds(true);
        postRecycler.setAdapter(adapter);

        getPostList(currentTimeMethod());

        postRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                if (itemTotalCount > 3) {
                    if (lastVisibleItemPosition == itemTotalCount) {
                        Log.d(TAG, "last Position...");
                        try {
                            getPostList(lastDate);
                        } catch (Exception e) {
                            Log.d(TAG, "onScrolled error: ");
                            e.printStackTrace();
                        }

                    } else if (lastVisibleItemPosition == -1) {
                        //  Log.d(TAG, "onScrolled: -1");
                    }
                }
            }
        });


        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    int no;

                    if (adapter.getItemCount() > 0) {
                        no = (int) adapter.getItemId(0);
                    } else {
                        no = 0;
                    }

                    Log.d(TAG + "Refresh", "onRefresh Num: " + no);

                    Call<List<PostData>> call = MyApplication.conn.retrofitService.onPullToRefresh(no);

                    call.enqueue(new Callback<List<PostData>>() {
                        @Override
                        public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {

                            List<PostData> pdList = response.body();

                            Log.d(TAG, "onResponse: " + pdList.size());

                            if (pdList.size() > 0) {
                                for (int i = 0; i < pdList.size(); i++) {
                                    adapter.updateItem(i, pdList.get(i));
                                    adapter.notifyDataSetChanged();
                                }

                                mSwipeRefreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(getActivity(), "새로운 피드백이 없습니다.", Toast.LENGTH_SHORT).show();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<PostData>> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(getActivity(), "Error.", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    public void getPostList(String date) {

        Log.d(TAG, "2. getPostList");

        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("로딩 중...");

        Call<List<PostData>> call = MyApplication.conn.retrofitService.loadGlobalPost(MyApplication.USER_ID, date, cnt);

        call.enqueue(new Callback<List<PostData>>() {
            @Override
            public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {

                List<PostData> pdList = response.body();

                if (pdList.size() > 0) {
                    progressDialog.show();

                    for (int i = 0; i < pdList.size(); i++) {
                        adapter.addItem(pdList.get(i));
                        adapter.notifyDataSetChanged();
                    }
                }

                if (adapter.getItemCount() > 0) {
                    if (pdList.size() > 0)
                        lastDate = pdList.get(pdList.size() - 1).getcDate();
                    else
                        Toast.makeText(getActivity(), "마지막입니다.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<PostData>> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    public String currentTimeMethod() {

        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(date);
    }

}
