package com.example.memorialpoint.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.memorialpoint.ImageEditor;
import com.example.memorialpoint.R;

import java.io.File;

public class MarkerImageFragment extends android.support.v4.app.Fragment {

    String TAG = "TAG";
    //ViewPager

    ImageView imgView;
    Button albumBtn, cameraBtn;
    // 이미지 업로드
    Uri postUri;
    Uri uriPath;
    Uri absoluteResultUri = null;
    String absolutePath;

    final int PICK_FROM_ALBUM = 1, PICK_FROM_CAMERA = 2, EDIT_FROM_IMG = 3;

    public interface OnSendUriListener {
        void onSendUri(Uri uri);
    }

    private OnSendUriListener onSendUriListener;

    public MarkerImageFragment() {
        // Required empty public constructor
    }

    //초기화 시 프래그먼트는 빈 생성자를 가져야하기에 데이터 생성자 역할을 대신한다.
    public static MarkerImageFragment getInstance(Uri postUri) {

        MarkerImageFragment imageFragment = new MarkerImageFragment();

        Bundle args = new Bundle();
        args.putParcelable("sendUri", postUri);

        imageFragment.setArguments(args);

        return imageFragment;
    }

    public Parcelable getShownUri() {
        return getArguments().getParcelable("sendUri");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof OnSendUriListener) {
            onSendUriListener = (OnSendUriListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_marker_fragment_first, container, false);

        if (getArguments() != null) {
           // postUri = (Uri) this.getArguments().getParcelable("sendUri");

            postUri = (Uri) getShownUri();

            if (postUri != null) {
                imgView.setImageURI(postUri);
                Log.d(TAG, "onCreateView: 값이 있네요");
            } else {
                Log.d(TAG, "onCreateView: 값이 없네요");
            }
        }
        imgView = (ImageView) view.findViewById(R.id.imgView);
        albumBtn = (Button) view.findViewById(R.id.albumBtn);
        cameraBtn = (Button) view.findViewById(R.id.cameraBtn);

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumIntent = new Intent(Intent.ACTION_PICK);
                albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(albumIntent, PICK_FROM_ALBUM);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File photoFile = createDir();

                        if (photoFile != null) {

                            if (Build.VERSION.SDK_INT >= 24) {
                                Uri providerPath = FileProvider.getUriForFile
                                        (getActivity(), getActivity().getPackageName() + ".file_provider", photoFile);

                                uriPath = providerPath;
                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                            } else {
                                uriPath = Uri.fromFile(photoFile);
                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
                                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "외장 메모리 미지원", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_FROM_ALBUM:
                try {
                    Uri imgUri = data.getData();

                    Intent editIntent = new Intent(getActivity(), ImageEditor.class);
                    editIntent.putExtra("imgUri", imgUri);
                    startActivityForResult(editIntent, EDIT_FROM_IMG);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            case PICK_FROM_CAMERA:
                try {
                    Intent syncIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File absolutionFile = new File(absolutePath);

                    uriPath = Uri.fromFile(absolutionFile);

                    syncIntent.setData(uriPath);
                    getActivity().sendBroadcast(syncIntent);

                    if (Build.VERSION.SDK_INT >= 24) {
                        Uri providerPath = FileProvider.getUriForFile
                                (getActivity(), getActivity().getPackageName() + ".file_provider", absolutionFile);

                        uriPath = providerPath;

                        syncIntent.setData(uriPath);
                        getActivity().sendBroadcast(syncIntent);
                    } else {
                        uriPath = Uri.fromFile(absolutionFile);

                        syncIntent.setData(uriPath);
                        getActivity().sendBroadcast(syncIntent);
                    }

                    Intent editIntent = new Intent(getActivity(), ImageEditor.class);
                    editIntent.putExtra("imgUri", uriPath);
                    startActivityForResult(editIntent, EDIT_FROM_IMG);

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            case EDIT_FROM_IMG:
                try {
                    Uri resultUri = data.getParcelableExtra("result");

                    if (resultUri != null) {

                        Intent syncIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        syncIntent.setData(resultUri);
                        getActivity().sendBroadcast(syncIntent);

                        absoluteResultUri = resultUri;
                        if (onSendUriListener != null) {
                            onSendUriListener.onSendUri(absoluteResultUri);
                        }
                        imgView.setImageURI(resultUri);
                    } else {
                        Toast.makeText(getActivity(), "이미지가 정상적으로 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "이미지가 정상적으로 등록되지 않았습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
        }
    }
}
