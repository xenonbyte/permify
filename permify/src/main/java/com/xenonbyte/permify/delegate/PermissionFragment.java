package com.xenonbyte.permify.delegate;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;

import com.xenonbyte.permify.PermissionResultStore;

import java.util.Map;


/**
 * 权限Fragment实现
 * 基于{@link Fragment}
 *
 * @author xubo
 */
public class PermissionFragment extends Fragment implements PermissionFragmentHost {
    private final PermissionResultStore<ActivityResultCallback<Map<String, Boolean>>> mPermissionResultStore = new PermissionResultStore<>();
    private final PermissionRequestManager mPermissionRequestManager = new PermissionRequestManager();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionRequestManager.onCreate();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPermissionRequestManager.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPermissionRequestManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPermissionRequestManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPermissionRequestManager.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPermissionRequestManager.onDestroy();
        mPermissionResultStore.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Boolean> resultMap = new ArrayMap<>();
        for (int i = 0; i < permissions.length; i++) {
            resultMap.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
        ActivityResultCallback<Map<String, Boolean>> resultCallback = mPermissionResultStore.getAndRemove(requestCode);
        if (resultCallback != null) {
            resultCallback.onActivityResult(resultMap);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mPermissionRequestManager.getLifecycle();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionRequestManager.executeRequestAction(() -> {
            mPermissionResultStore.save(requestCode, resultCallback);
            requestPermissions(perms, requestCode);
        });
    }

}