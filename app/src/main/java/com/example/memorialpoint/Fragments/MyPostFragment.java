package com.example.memorialpoint.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.example.memorialpoint.Adapters.MyPostRecyclerAdapter;
import com.example.memorialpoint.DialogSet;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.MyApplication;
import com.example.memorialpoint.MyPost;
import com.example.memorialpoint.R;
import com.example.memorialpoint.TabMain;
import com.example.memorialpoint.Util.CustomPopUpMenu;
import com.naver.maps.map.overlay.Marker;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostFragment extends Fragment {

    String TAG = "Memorial.MyPostFragment.";

    View view;

    //화면 구성 리사이클러 - 어댑터
    RecyclerView myRv;
    MyPostRecyclerAdapter myPostAdapter;

    //My Post의 값을 가지고 있는 리스트 - 받아올 떄 PostData 중 이미지, 포스트 넘버(고유), 아이디만 받아온다
    ArrayList<PostData> myPostList = new ArrayList<>();

    CustomPopUpMenu customPop;
    PopupMenu popupMenu;

    //리사이클러 뷰에 들어갈 My Post 데이터를 불러오는 객체
    Call<List<PostData>> postCall;

    public MyPostFragment() {
        // Required empty public constructor
    }

    //리사이클러 뷰의 설정 및 객체화 부분
    public void setMyRv(View view, ArrayList<PostData> arr) {

        myRv = view.findViewById(R.id.mp_my_recyclerView);

        //LayoutManager - staggered GridLayout 사용
        myRv.setLayoutManager(getStaggeredGridLayout());

        //리사이클러 뷰 아이템 간의 간격 한줄 당 3개의 아이템과 최소한 간격
        myRv.addItemDecoration(new GridSpacingItemDecoration(3, 5, true));

        myPostAdapter = new MyPostRecyclerAdapter(arr, getActivity());
        myRv.setAdapter(myPostAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_post, container, false);

        //My Post 데이터를 불러오는 통신 작업
        postCall = MyApplication.conn.retrofitService.loadMyPosts(MyApplication.USER_ID);

        // 성공 할 시 값을 넣어주고 실패 할 시 빈 리스트를 넣어준다.
        postCall.enqueue(new Callback<List<PostData>>() {
            @Override
            public void onResponse(Call<List<PostData>> call, Response<List<PostData>> response) {

                if (response.body() != null)
                    myPostList = (ArrayList<PostData>) response.body();
                else
                    myPostList = new ArrayList<>();

                setMyRv(view, myPostList);

                //아이템 클릭 시 이벤트
                myPostAdapter.setOnPostClickListener(new MyPostRecyclerAdapter.OnPostClickListener() {
                    @Override
                    public void onPostClicked(PostData postData, int position) {
                        Log.d(TAG, "onPostClicked: " + postData.getWriter());
                        getMyPost(postData.getNo(), position);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<PostData>> call, Throwable t) {
                myPostList = new ArrayList<>();

                setMyRv(view, myPostList);

                MyApplication.sendToast(getActivity(), "에러로 인해 불러오기 실패");
            }
        });

        return view;
    }

    //아이템 클릭 시 해당 포스트가 다이얼로그 형식으로 출력된다.
    public void getMyPost(int no, int position) {

        //해당 포스트의 데이터를 불러오는 통신 객체
        Call<PostData> postCall = MyApplication.conn.retrofitService.loadOnePost(no);

        postCall.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {

                PostData postData = response.body();

                DialogSet ds = new DialogSet(getActivity());
                ds.localPost();

                //프로필 이미지 삽입
                MyApplication.ViewCircuitCrop(getActivity(), MyApplication.PROFILE_IMAGE, ds.postHostImg);

                //포스트 작성자 아이디
                ds.postHostID.setText(MyApplication.USER_ID);
                ds.postHostID.setSelected(true);

                customPop = new CustomPopUpMenu(getActivity(), ds.menuIcon);
                popupMenu = customPop.showPopUp(R.menu.my_post_menu);

                //포스트 우측 상단의 팝업 메뉴
                ds.menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();

                                switch (id) {
//                                    case R.id.pm_move:
//                                        MyApplication.sendToast(mContext, item.getTitle().toString());
//                                        return true;
                                    case R.id.pm_remove:
                                        Call<ResponseData> removeCall = MyApplication.conn.retrofitService.removePost(postData.getNo());
                                        removeCall.enqueue(new Callback<ResponseData>() {
                                            @Override
                                            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                                myPostAdapter.removeItem(position);
                                                myPostAdapter.notifyItemRemoved(position);

                                                TabMain.mainViewPager.getAdapter().notifyDataSetChanged();

                                                ds.lpDlg.dismiss();

                                            }

                                            @Override
                                            public void onFailure(Call<ResponseData> call, Throwable t) {
                                                MyApplication.sendToast(getActivity(), "삭제 실패");
                                            }
                                        });

                                        MyApplication.sendToast(getActivity(), item.getTitle().toString());
                                        return true;
//                                    case R.id.pm_revise:
//                                        MyApplication.sendToast(mContext, item.getTitle().toString());
//                                        return true;
                                    default:
                                        return true;

                                }
                            }
                        });

                        popupMenu.show();
                    }
                });

                //포스트 메인 이미지 삽입
                try {

                    String path = MyApplication.ip + "memorial_point" + postData.getUri().substring(2);
                    URL tmpUrl = new URL(path);

                    Glide.with(getActivity())
                            .load(tmpUrl)
                            .placeholder(R.drawable.p_empty_image)
                            .error(R.drawable.p_empty_image)
                            .into(ds.postImg);

                } catch (Exception e) {
                    Log.d(TAG, "run error: ");
                    e.printStackTrace();
                }

                //주소, 상세 주소, 내용들의 컨텍츠를 삽입

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                    // noinspection deprecation
                    if (postData.getcAddress() != null)
                        ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "   " + postData.getcAddress()));
                    else
                        ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + "-"));


                    if (postData.getdAddress() != null)
                        ds.dAddressText.setText(Html.fromHtml("<b>" + "상세" + "</b>" + "   " + postData.getdAddress()));
                    else
                        ds.dAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + "-"));


                    ds.contentText.setText(Html.fromHtml("<b>" + postData.getWriter() + "</b>" + "  " + postData.getContents()));
                } else {
                    if (postData.getcAddress() != null)
                        ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + postData.getcAddress(), Html.FROM_HTML_MODE_LEGACY));
                    else
                        ds.cAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  " + "-", Html.FROM_HTML_MODE_LEGACY));

                    if (postData.getdAddress() != null)
                        ds.dAddressText.setText(Html.fromHtml("<b>" + "상세" + "</b>" + "  " + postData.getdAddress(), Html.FROM_HTML_MODE_LEGACY));
                    else
                        ds.dAddressText.setText(Html.fromHtml("<b>" + "주소" + "</b>" + "  "+ "-", Html.FROM_HTML_MODE_LEGACY));

                    ds.contentText.setText(Html.fromHtml("<b>" + postData.getWriter() + "</b>" + "  " + postData.getContents(), Html.FROM_HTML_MODE_LEGACY));
                }

                if (postData.getFriend() != null) {
                    ds.contentText.append("\n" + postData.getFriend());
                }

                if (postData.getHashTag() !=null) {
                    ds.contentText.append("\n" + postData.getHashTag());
                }

            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });

    }

    //리사이클러뷰에 넣을 레이아웃 매니저 - 미사용 중
    public GridLayoutManager getGridLayout() {
        //그리드 레이아웃 매니저 설정 격자 패턴
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 6);
        //아이템을 2/3/2/3/..패턴으로 화면에 출력한다.
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int gridPosition = position % 5;

                switch (gridPosition) {
                    case 0:
                    case 1:
                    case 2:
                        return 2;
                    case 3:
                    case 4:
                        return 3;
                }
                return 0;
            }
        });

        return gridLayoutManager;
    }

    //리사이클러뷰에 넣을 레이아웃 매니저 - 사용 중
    public StaggeredGridLayoutManager getStaggeredGridLayout() {
        //그리드 레이아웃 매니저 설정 격자 패턴
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 0);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        staggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);

        return staggeredGridLayoutManager;
    }

}

class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    //단수,패딩값,
    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
           /* if(view instanceof ViewModel == false){ //홈 그리드 아이템이 아닌경우 패스
                return;
            }*/
        int position = parent.getChildAdapterPosition(view); // item position

        int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();

        if (includeEdge) {


            if (spanIndex == 0) {
                //좌측 아이템이며 우측 패딩을 설정한 패딩의 1/2로 설정
                outRect.left = spacing;
                outRect.right = spacing / spanCount;
            } else {//if you just have 2 span . Or you can use (staggeredGridLayoutManager.getSpanCount()-1) as last span
                //우측 아이템이며 좌측 패딩을 설정한 패딩의 1/2로 설정
                outRect.left = spacing / spanCount;
                outRect.right = spacing;
            }

            //상단 패딩
            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }

            //하단 패딩
            outRect.bottom = spacing; // item bottom
        }
    }
}

