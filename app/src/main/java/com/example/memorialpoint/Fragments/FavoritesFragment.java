package com.example.memorialpoint.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.memorialpoint.Adapters.PostRecyclerAdapter;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.MyPost;
import com.example.memorialpoint.R;
import com.example.memorialpoint.TabMain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {

    String TAG = "Memorial.FavoritesFragment.";

    View view;

    //즐겨찾기 리사이클러뷰
    RecyclerView favoritesRv;
    PostRecyclerAdapter favoritesAdapter;

    //한번에 가져올 포스트 갯수
    int cnt = 5;
    boolean isRefresh = false;

    //값을 가져올 때 기준 날짜
    String lastDate;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();

        return fragment;
    }

    public void setFavoritesRv(View view) {

        favoritesRv = view.findViewById(R.id.mp_favorites_recyclerView);
        favoritesRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoritesAdapter = new PostRecyclerAdapter(getActivity());
        favoritesAdapter.setHasStableIds(true);
        favoritesRv.setAdapter(favoritesAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);

        setFavoritesRv(view);

        getPostList(currentTimeMethod());

        getDownPost(favoritesRv);

        favoritesAdapter.setOnLikeClickListener(new PostRecyclerAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClicked(int position) {
                favoritesAdapter.removeItem(position);
                favoritesAdapter.notifyItemRemoved(position);

               TabMain.mainViewPager.getAdapter().notifyDataSetChanged();
            }
        });

        return view;
    }

    public void getPostList(String date) {

        Log.d(TAG + "getPostList", "Selected Date: " + date);

        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("로딩 중...");

        Call<List<PostData>> favoritesCall = MyApplication.conn.retrofitService.loadFavoritesPost(MyApplication.USER_ID, date, cnt);

        favoritesCall.enqueue(new Callback<List<PostData>>() {
            @Override
            public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {

                List<PostData> pdList = response.body();

                if (pdList.size() > 0) {
                    progressDialog.show();

                    for (int i = 0; i < pdList.size(); i++) {
                        favoritesAdapter.addItem(pdList.get(i));
                        favoritesAdapter.notifyDataSetChanged();
                    }
                }

                if (favoritesAdapter.getItemCount() > 0) {
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

    public void getDownPost(RecyclerView rv) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
    }

    public String currentTimeMethod() {

        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(date);
    }
}
