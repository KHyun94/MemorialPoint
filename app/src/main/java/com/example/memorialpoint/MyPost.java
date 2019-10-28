package com.example.memorialpoint;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.memorialpoint.Fragments.FavoritesFragment;
import com.example.memorialpoint.Fragments.MyPostFragment;
import com.example.memorialpoint.Fragments.NMapFragment;
import com.example.memorialpoint.Fragments.PostFragment;
import com.example.memorialpoint.Fragments.RoomManagerFragment;

public class MyPost extends AppCompatActivity {


    TabLayout mp_tabLayout;
    NoneSwipeViewPager mp_viewPager;

    MyPostAdapter myPostAdapter;

    private void init(){
        mp_tabLayout = findViewById(R.id.mp_tabLayout);
        mp_viewPager = findViewById(R.id.mp_viewPager);
    }

    //탭 설정
    private void setTab(TabLayout tab){
        tab.addTab(tab.newTab().setIcon(R.drawable.p_my_icon).setText("My Post"));
        tab.addTab(tab.newTab().setIcon(R.drawable.p_star_icon).setText("Favorites"));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        init();

        setTab(mp_tabLayout);

        //ViewPager 설정 + ViewPager와 TabLayout 연결 작업
        myPostAdapter = new MyPostAdapter(getSupportFragmentManager(), 2);
        mp_viewPager.setAdapter(myPostAdapter);
        mp_viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mp_tabLayout));

        //탭 클릭 시 이벤트 처리 파트
        mp_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mp_viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }
}

class MyPostAdapter extends FragmentStatePagerAdapter {

    int numOfPage;

    public MyPostAdapter(FragmentManager fm, int numOfPage) {
        super(fm);
        this.numOfPage = numOfPage;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MyPostFragment myPostFragment = new MyPostFragment();
                return myPostFragment;
            case 1:
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                return favoritesFragment;

            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return numOfPage;
    }
}

