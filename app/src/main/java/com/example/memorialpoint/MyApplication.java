package com.example.memorialpoint;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.memorialpoint.Networks.RetrofitCon;

public class MyApplication extends Application {

    public static String ip = "";
    static String TAG = "TAG";
    public static String USER_ID = "";
    static String USER_EMAIL = "";
    static Context mContext = null;
    public static RetrofitCon conn = null;
    public static Bitmap PROFILE_IMAGE = null;

    static InputMethodManager imm = null;

    static int DEVISE_WIDTH = 0;
    static int DEVISE_HEIGHT = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        ip = getResources().getString(R.string.ip);
        conn = new RetrofitCon(ip);

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

        DEVISE_WIDTH = displayMetrics.widthPixels;
        DEVISE_HEIGHT = displayMetrics.heightPixels;

        PROFILE_IMAGE = ((BitmapDrawable) getResources().getDrawable(R.drawable.p_nmap_blank_person, getTheme())).getBitmap();
    }

    public static void hideToKeyBoard(Context context, View v){
        v.clearFocus();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showToKeyBoard(Context context, View v){
        v.requestFocus();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY );
    }

    public static void ButtonCircuitCrop(Context context, Object o, ImageButton v){
        Glide.with(context).load(o).circleCrop().into(v);
    }

    public static void ViewCircuitCrop(Context context, Object o, ImageView v){
        Glide.with(context).load(o).circleCrop().into(v);
    }

    public static void sendToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("TAG", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d("TAG", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
