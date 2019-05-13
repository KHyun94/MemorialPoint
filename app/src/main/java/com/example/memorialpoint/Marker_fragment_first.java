package com.example.memorialpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;


public class Marker_fragment_first extends Fragment {
    Activity act;
    Context mContext;

    String TAG = "TAG";
    //ViewPager

    ImageView placeImg;
    Button albumBtn, cameraBtn;
    // 이미지 업로드
    Uri uriPath;
    String absolutePath;
    Bitmap bitmap;
    final int PICK_FROM_ALBUM = 1, PICK_FROM_CAMERA = 2, EDIT_FROM_IMG = 3;

    public Marker_fragment_first() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_marker_fragment_first, container, false);

        placeImg = (ImageView) view.findViewById(R.id.placeImg);
        albumBtn = (Button) view.findViewById(R.id.albumBtn);
        cameraBtn = (Button) view.findViewById(R.id.cameraBtn);
        albumBtn = (Button) view.findViewById(R.id.albumBtn);

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
                Log.d(TAG, "마커 앨범");
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File photoFile = createDir();

                    if (photoFile != null) {

                        if (Build.VERSION.SDK_INT >= 24) {
                            Log.d(TAG, "onClick: 24버전 이상");
                            Uri providerPath = FileProvider.getUriForFile
                                    (mContext, getActivity().getPackageName() + ".file_provider", photoFile);

                            uriPath = providerPath;
                            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                            startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                        } else {
                            Log.d(TAG, "onClick: 24버전 이하");
                            uriPath = Uri.fromFile(photoFile);
                            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                            startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                        }
                    }

                } else {
                    Toast.makeText(mContext, "외장 메모리 미지원", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "마커 카메라");
            }
        });

        return view;
    }

    public File createDir() {

        Log.d(TAG, "마커 디렉토리 생성 맟 파일 생성");
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode);
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                try {
                    Log.d(TAG, "마커 앨범 결과");
                    Uri imgUri = data.getData();
                    Log.d(TAG, "마커 앨범 결과 Uri: " + imgUri.getPath());
                    Intent editIntent = new Intent(getActivity(), Editing_Img.class);
                    editIntent.putExtra("imgUri", imgUri);
                    startActivityForResult(editIntent, EDIT_FROM_IMG);

                    break;
                } catch (Exception e) {
                    break;
                }

            case PICK_FROM_CAMERA:
                try {
                    Log.d(TAG, "마커 카메라 결과");
                    Intent syncIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File absolution_file = new File(absolutePath);

                    uriPath = Uri.fromFile(absolution_file);

                    syncIntent.setData(uriPath);
                    getActivity().sendBroadcast(syncIntent);

                    if (Build.VERSION.SDK_INT >= 24) {
                        Log.d(TAG, "onClick: 24버전 이상");
                        Uri providerPath = FileProvider.getUriForFile
                                (mContext, getActivity().getPackageName() + ".file_provider", absolution_file);

                        uriPath = providerPath;

                        syncIntent.setData(uriPath);
                        getActivity().sendBroadcast(syncIntent);
                    } else {
                        Log.d(TAG, "onClick: 24버전 이하");
                        uriPath = Uri.fromFile(absolution_file);

                        syncIntent.setData(uriPath);
                        getActivity().sendBroadcast(syncIntent);
                    }

                    Log.d(TAG, "마커 카메라 결과 Uri: " + uriPath.getPath());
                    Intent intent_edit = new Intent(getActivity(), Editing_Img.class);
                    intent_edit.putExtra("imgUri", uriPath);
                    startActivityForResult(intent_edit, EDIT_FROM_IMG);

                    break;
                } catch (Exception e) {
                    break;
                }

            case EDIT_FROM_IMG:
                Log.d(TAG, "마커 에디팅");
                try{
                    Uri resultUri = data.getParcelableExtra("result");

                    if (resultUri != null) {

                        Log.d(TAG, "넘어온 결과값: " + resultUri);

                        Intent sync = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        sync.setData(resultUri);
                        getActivity().sendBroadcast(sync);

                        placeImg.setImageURI(resultUri);

                    } else {
                        Toast.makeText(mContext, "이미지가 정상적으로 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                }
                catch (NullPointerException e)
                {
                    Toast.makeText(mContext, "이미지가 정상적으로 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                break;

        }
    }
}