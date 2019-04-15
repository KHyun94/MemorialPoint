package com.example.memorialpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
    final int PICK_FROM_ALBUM = 1, PICK_FROM_CAMERA = 2;

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
                Log.d(TAG, "onClick: 앨범");

            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = createDir();

                if (photoFile != null) {
                    Uri providerPath = FileProvider.getUriForFile
                            (mContext, getActivity().getPackageName() + ".file_provider", photoFile);

                    uriPath = providerPath;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerPath);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                    Log.d(TAG, "onClick: 카메라");
                }
            }
        });

        return view;
    }

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

    public void BitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String img_base64 = Base64.encodeToString(arr, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode);
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                try {
                    Uri imgUri = data.getData();
                    placeImg.setImageURI(imgUri);
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);
                    break;
                } catch (Exception e) {
                    break;
                }

            case PICK_FROM_CAMERA:
                try {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File absolution_file = new File(absolutePath);
                    Uri contentUri = Uri.fromFile(absolution_file);
                    intent.setData(contentUri);
                    getActivity().sendBroadcast(intent);
                    placeImg.setImageURI(contentUri);
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
                } catch (Exception e) {
                    break;
                }

        }
    }
}
