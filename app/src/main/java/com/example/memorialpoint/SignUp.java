package com.example.memorialpoint;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.example.memorialpoint.Fragments.EmailAuthFragment;
import com.example.memorialpoint.Fragments.SignUpFragment;

public class SignUp extends AppCompatActivity {

    Context mContext;

    NoneSwipeViewPager viewPager;
    signUpPagerAdapter adapter;

    int numOfPage = 2;
    String email, authenticationStr;

    Bundle bundle;

    public interface onKeyBackPressListener {
        void onBack();
    }
    private onKeyBackPressListener mOnKeyBackPressListener;

    public void setOnKeyBackPressListener(onKeyBackPressListener listener) {
        mOnKeyBackPressListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        viewPager = (NoneSwipeViewPager) findViewById(R.id.signUpViewPager);

        email = getIntent().getStringExtra("email");
        authenticationStr = getIntent().getStringExtra("authentication");

        bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("authentication", authenticationStr);

        mContext = this;

        adapter = new signUpPagerAdapter(getSupportFragmentManager(), numOfPage, bundle);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    public void nextPage() {
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mOnKeyBackPressListener != null)
        {
            mOnKeyBackPressListener.onBack();
        }

    }
}

class signUpPagerAdapter extends FragmentStatePagerAdapter {

    int numOfPage;
    private Bundle sendData;

    public signUpPagerAdapter(FragmentManager fm, int numOfPage, Bundle data) {
        super(fm);
        this.numOfPage = numOfPage;
        this.sendData = data;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                EmailAuthFragment frag1 = new EmailAuthFragment();
                frag1.setArguments(sendData);
                return frag1;

            case 1:
                SignUpFragment frag2 = new SignUpFragment();
                frag2.setArguments(sendData);
                return frag2;
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