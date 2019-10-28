package com.example.memorialpoint.Settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.Permission;
import java.util.ArrayList;

public class Setting_permissionCheck {

    Context mContext;
    Activity activity;

    public Setting_permissionCheck(Context mContext, Activity activity) {
        this.mContext = mContext;
        this.activity = activity;
    }

    public void setPermissionListener_location(PermissionListener permissionListener) {
        TedPermission.with(mContext)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setRationaleMessage("현재 위치 확인 및 등록 서비스를 이용하실려면 \n권한을 승인해주시길 바랍니다.")
                .setRationaleTitle("위치 권한 승인")
                .setDeniedMessage("위치 권한이 미승인되었습니다. \n수동: [설정] > [권한] > [위치]")
                .setDeniedTitle("위치 권한 미승인")
                .check();
    }

    public void setPermissionListener_camera(PermissionListener permissionListener){
        TedPermission.with(mContext)
                .setRationaleTitle("카메라 권한 승인")
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .setRationaleMessage("카메라 기능을 이용하실려면\n카메라 권한과 외부 저장소 권한을 승인해주시길 바랍니다.")
                .setDeniedTitle("카메라 권한 미승인")
                .setDeniedMessage("카메라 권한이 미승인되었습니다. \n수동: [설정] > [권한] > [카메라]")
                .check();

    }
}


