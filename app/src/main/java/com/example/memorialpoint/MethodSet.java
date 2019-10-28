package com.example.memorialpoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorialpoint.Models.ResponseData;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MethodSet {

    //이미지 리사이즈
    public static Uri resize(Context context,Uri uri,int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resizeBitmap, "Title", null);

        return Uri.parse(path);
    }

    //이미지 업로드
    public static void uploadImg(Context context, Uri uri) {

        File file = new File(uri.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData
                        ("upload_image", file.getName(), requestFile);

        Call<ResponseData> call = MyApplication.conn.retrofitService.uploadImage(MyApplication.USER_ID, body);

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ResponseData responseData = response.body();

                if (responseData.getResponseData().equals("ok")) {
                    Toast.makeText(context, "정상적으로 등록이 되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "등록에 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(context, "통신 실패: ", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    //뷰 사이즈에 따라 텍스트 사이즈 조정
    public static void autoTextSize(TextView tv, String context) {

        Rect rect = new Rect();

        tv.setText(context);
        tv.getPaint().getTextBounds(tv.getText().toString(), 0, tv.getText().length(), rect);
        tv.setTextSize(rect.height());
    }

    //뷰 사이즈에 따라 에디트텍스트 사이즈 조정
    public static void autoEditSize(EditText et, String context) {

        Rect rect = new Rect();

        et.setText(context);
        et.getPaint().getTextBounds(et.getText().toString(), 0, et.getText().length(), rect);
        et.setTextSize(rect.height());
    }
}
