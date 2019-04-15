package com.example.memorialpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Editing_Img extends AppCompatActivity {

    //액티비티
    Activity act;
    Context mContext;

    //툴바
    RelativeLayout topLayout;
    ImageButton top_backBtn, top_saveBtn;

    Toolbar bottomToolbar;

    //메인 이미지
    ImageView edit_img;

    Uri uri;
    String absolutePath;
    Bitmap tmp;

    final int CROP_FROM_IMAGE = 111, ROTATION_FROM_IMAGE = 222, RETURN_EDITING = 3;
    int barHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_img);

        act = this;
        mContext = this;

        topLayout = (RelativeLayout) findViewById(R.id.topLayout);
        //  topToolbar = (Toolbar) findViewById(R.id.topToolbar);
        bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar);
        edit_img = (ImageView) findViewById(R.id.edit_img);
        top_backBtn = (ImageButton) findViewById(R.id.top_back_btn);
        top_saveBtn = (ImageButton) findViewById(R.id.top_save_btn);

        Intent intent = getIntent();
        String uriString = intent.getStringExtra("imgUri");

        uri = Uri.parse(uriString);

        try {
            tmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        edit_img.setImageBitmap(tmp);

        setSupportActionBar(bottomToolbar);

        top_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        top_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent saveIntent = new Intent();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), tmp, "Title", null);
                intent.putExtra("editImg", path);
                setResult(RESULT_OK, saveIntent);
                finish();
            }
        });
    }


/*    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        barHeight = topLayout.getHeight();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(barHeight, barHeight);
        top_backBtn.setLayoutParams(lp);
        top_saveBtn.setLayoutParams(lp);
    }*/


    // 디렉토리 생성
    public File createDir() {

        //저장될 파일의 이름
        String imgName = "mePo_" + System.currentTimeMillis() + ".jpg";

        //저장 디렉토리 주소
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/MemorialPoint_Photo");

        //디렉토리가 없을 시 생성
        if (!storageDir.exists())
            storageDir.mkdirs();

        //저장될 파일의 주소
        File imgFilePath = new File(storageDir, imgName);
        absolutePath = imgFilePath.getAbsolutePath();

        return imgFilePath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Menu bottomBar = bottomToolbar.getMenu();

        bottomToolbar.setTitle("");

        getMenuInflater().inflate(R.menu.editing_bottom_toolbar, bottomBar);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_crop:

                File photoFile = createDir();
               /* tmp = ((BitmapDrawable)edit_img.getDrawable()).getBitmap();
                uri = Uri.parse(BitMapToString(tmp));
*/
                Uri provider_uri = Uri.fromFile(photoFile);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra("scale", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, provider_uri);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;

            case R.id.action_rotation:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case CROP_FROM_IMAGE:

                Uri tmp_uri = data.getData();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(tmp_uri);
                sendBroadcast(intent);

                try {
                    tmp = MediaStore.Images.Media.getBitmap(getContentResolver(), tmp_uri);

                    if (tmp != null) {
                        edit_img.setImageBitmap(tmp);
                        Log.d("TAG", "onActivityResult: " + edit_img.getDrawable().toString());
                        File f = new File(tmp_uri.getPath());

                        if (f.exists()) {
                            f.delete();
                            Log.d("TAG", "onActivityResult:  삭제성공");
                            Log.d("TAG", "onActivityResult: " + f.getAbsolutePath());
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case ROTATION_FROM_IMAGE:
                break;

        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] arr_ENCODE = baos.toByteArray();
        String img_base64 = Base64.encodeToString(arr_ENCODE, 0);

        return img_base64;
    }

    public Bitmap StringToBitMap(String img_base64) {
        byte[] arr_DECODE = Base64.decode(img_base64, 0);
        ByteArrayInputStream bais = new ByteArrayInputStream(arr_DECODE);
        Bitmap bitmap = BitmapFactory.decodeStream(bais);

        return bitmap;
    }
}
