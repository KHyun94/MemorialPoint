package com.example.memorialpoint;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

    Uri edited_Uri;
    Uri saveUri;
    Uri uriPath;

    Bitmap edited_Bitmap;
    String absolutePath;

    ArrayList<Uri> tmpArray;
    final int CROP_FROM_IMAGE = 111, RETURN_PAGE = 222;

    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_img);

        //액티비티
        act = this;
        mContext = this;

        //top-bar
        topLayout = (RelativeLayout) findViewById(R.id.topLayout);
        //bottom-bar
        bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar);
        //이미지뷰
        edit_img = (ImageView) findViewById(R.id.edit_img);
        //top-bar: 뒤로가기 & 저장하기
        top_backBtn = (ImageButton) findViewById(R.id.top_back_btn);
        top_saveBtn = (ImageButton) findViewById(R.id.top_save_btn);

        //이미지 받아오기 -> uri으로 받아온다.

        edited_Uri = getIntent().getParcelableExtra("imgUri");

        //이미지 등록
        edit_img.setImageURI(edited_Uri);

        tmpArray = new ArrayList<Uri>();

        setSupportActionBar(bottomToolbar);

        //뒤로 가기
        top_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < tmpArray.size(); i++) {
                    File removeFile = new File(tmpArray.get(i).getPath());

                    if (removeFile.getAbsoluteFile().exists()) {
                        if(removeFile.delete()){
                            Log.d(TAG, "편집 사진 삭제 완료");
                        }
                        else{
                            Log.d(TAG, "편집 사진 삭제 실패");
                        }
                    }
                }

                Intent backIntent = new Intent();
                backIntent.putExtra("value", false);
                setResult(RESULT_OK, backIntent);
                finish();
            }
        });

        top_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tmpArray.size() > 0)
                    saveUri = tmpArray.get(tmpArray.size() - 1);
                else
                    saveUri = edited_Uri;

                File photoFile = createDir();
                FileOutputStream fos = null;

                if (photoFile != null) {

                    if (Build.VERSION.SDK_INT >= 24) {
                        Log.d(TAG, "onClick: 24버전 이상");
                        Uri providerPath = FileProvider.getUriForFile
                                (mContext, getApplicationContext().getPackageName() + ".file_provider", photoFile);

                        uriPath = providerPath;
                    } else {
                        Log.d(TAG, "onClick: 24버전 이하");
                        uriPath = Uri.fromFile(photoFile);
                    }
                }

                try {
                    fos = new FileOutputStream(photoFile);
                    edited_Bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), edited_Uri);
                    edited_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < tmpArray.size(); i++) {
                    Log.d("TAG", i + 1 + "번째: " + tmpArray.get(i));
                }

                for (int i = 0; i < tmpArray.size(); i++) {
                    File removeFile = new File(tmpArray.get(i).getPath());

                    if (removeFile.getAbsoluteFile().exists()) {
                        if(removeFile.delete()){
                            Log.d(TAG, "편집 사진 삭제 완료");
                        }
                        else{
                            Log.d(TAG, "편집 사진 삭제 실패");
                        }
                    }
                }
                Uri returnUri = Uri.fromFile(photoFile);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("value", true); //올바른 절차 or 도중 cancel
                returnIntent.putExtra("result", returnUri);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    // 디렉토리 생성
    public File createDir() {

        //저장될 파일의 이름
        String imgName = "mePo_" + System.currentTimeMillis() + ".jpg";

        //저장 디렉토리 주소
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/MemorialPoint_Photo/");

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
        //bottom-bar
        bottomToolbar.setTitle("");
        getMenuInflater().inflate(R.menu.editing_bottom_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            //bottom-bar -> crop
            case R.id.action_crop:

                File photoFile = createDir();

                if (photoFile != null) {

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(edited_Uri, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("return-data", false);
                    intent.putExtra("scale", true);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile.getAbsolutePath());
                    startActivityForResult(intent, CROP_FROM_IMAGE);

                }

                break;

            case R.id.action_rotation:

                Matrix rotationMatrix = new Matrix();
                rotationMatrix.postRotate(90);

                try {
                    edited_Bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), edited_Uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                edited_Bitmap = Bitmap.createBitmap(edited_Bitmap, 0, 0,
                        edited_Bitmap.getWidth(), edited_Bitmap.getHeight(), rotationMatrix, false);

                try{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    edited_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), edited_Bitmap, "Title", null);
                    edited_Uri = Uri.parse(path);

                    Cursor c = getContentResolver().query(Uri.parse(path), null,null,null,null);
                    c.moveToNext();
                    String rmPath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                    c.close();

                    edit_img.setImageURI(edited_Uri);
                    tmpArray.add(Uri.parse(rmPath));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

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
                mContext.sendBroadcast(intent);

                edit_img.setImageURI(tmp_uri);

                edited_Uri = tmp_uri;

                Cursor c = getContentResolver().query(tmp_uri, null,null,null,null);
                c.moveToNext();
                String rmPath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                c.close();
                tmpArray.add(Uri.parse(rmPath));

                break;

        }
    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialog_Style);
        alert.setMessage("뒤로 돌아가시겠습니까?\n 진행 중인 이미지는 저장되지 않고 끝납니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < tmpArray.size(); i++) {
                            File removeFile = new File(tmpArray.get(i).getPath());

                            if (removeFile.getAbsoluteFile().exists()) {
                                if(removeFile.delete()){
                                    Log.d(TAG, "편집 사진 삭제 완료");
                                }
                                else{
                                    Log.d(TAG, "편집 사진 삭제 실패");
                                }
                            }
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();


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
